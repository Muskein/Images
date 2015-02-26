package com.example.images;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private File root, root2;
	private ArrayList<File> fileList = new ArrayList<File>();
	private LinearLayout view;
	int onemoreface=0;
	String s="";
	int countfaces;
	int dc =0;
	int fc =0;
	private static final int NUM_FACES = 6; // max is 64
private static final boolean DEBUG = true;

	//private FaceDetector arrayFaces;
	//private FaceDetector.Face getAllFaces[] = new FaceDetector.Face[NUM_FACES];
	

	//private PointF eyesMidPts[] = new PointF[NUM_FACES];
	//private float  eyesDistance[] = new float[NUM_FACES];

	private Bitmap sourceImage;

	//private Paint tmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	//private Paint pOuterBullsEye = new Paint(Paint.ANTI_ALIAS_FLAG);
	//private Paint pInnerBullsEye = new Paint(Paint.ANTI_ALIAS_FLAG);

	//private int picWidth, picHeight;
	//private float xRatio, yRatio;
//	int faces=0;
	TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.text);

Button b = (Button) findViewById(R.id.clicktodetect);
b.setOnClickListener(new OnClickListener()
{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		File dir = new File(Environment.getExternalStorageDirectory().getPath(),"MyFolder"); 
		DeleteRecursive(dir);
	    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();

		new backgroundTask().execute();
	}
});
	}

		class backgroundTask extends AsyncTask<Void, String, Void> 
			{
			

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				root = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() );
				getfile(root);
				for (int i = 0; i < fileList.size(); i++) {
					System.out.println(fileList.get(i).getAbsolutePath());
					if (fileList.get(i).isDirectory()) 
					{
						dc++;		
						
						s = s+ "DIRECTORY " + dc + " " + fileList.get(i).getName();
						
					}
					else
					{
						compressImage(fileList.get(i).getAbsolutePath());
	publishProgress(i+ "/" + fileList.size() + " files detected for Image Compression!");
						//int n = checkforface(fileList.get(i).getAbsolutePath());
			//			fc++;
						
				//		s = fc + " "+s+ fileList.get(i).getName();
						//s= s + fileList.get(i).getAbsolutePath() + " & faces -" + n + "\n";
					}
				}
				
				
				return null;

			
			}

			
protected void onProgressUpdate(String... values)
{
	textView.setText(values[0]);
}		
    
protected void onPostExecute(Void result) 
{
	//	textView.setText(s);
//	Toast.makeText(getApplicationContext(), "File count" + fc +"\nDirectory Count " + dc, Toast.LENGTH_SHORT).show();
    	Toast.makeText(getApplicationContext(), "All Images Compressed!", Toast.LENGTH_SHORT).show();
    	new faceDetect().execute();
}



}

class faceDetect extends AsyncTask<Void,String,Void>
{

	@Override
	protected Void doInBackground(Void... params) {
		 File root2;
		 root2 = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/MyFolder/Images" );
		 empty();
			
		 getfile(root2);
		for (int i = 0; i < fileList.size(); i++) {
		
			if (fileList.get(i).isDirectory()) 
			{
			}
			else
			{

				int n = checkforface(fileList.get(i).getAbsolutePath());
				publishProgress(i+ "/" + fileList.size() + " images tested for face detection!");
s= s + fileList.get(i).getAbsolutePath() + " & faces -" + n + "\n";
			}
		}
		return null;
	
	}
    protected void onPostExecute(Void result) {
	textView.setPadding(5, 5, 5, 5);
    	textView.setText(s);
    }
    protected void onProgressUpdate(String... values)
    {
    	textView.setText("All Images Compressed!\n" + values[0]);
    }
	
}
public void empty()
{
fileList.clear();
}

public ArrayList<File> getfile(File dir) {
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
		for (int i = 0; i < listFile.length; i++) {
		if (listFile[i].isDirectory()) {
//		fileList.add(listFile[i]);
		getfile(listFile[i]);					
		} 
		else 
		{
					if (listFile[i].getName().endsWith(".png")
							|| listFile[i].getName().endsWith(".jpg")
							|| listFile[i].getName().endsWith(".jpeg")
							|| listFile[i].getName().endsWith(".gif"))
					{
						fileList.add(listFile[i]);
					}
				}
			}
		}
		return fileList;
	}
	
	public int checkforface(String path)
	{
		countfaces=0;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;

        
        //********This code imports the image from the SD card which does not work
        String iD = path;
//        System.out.println(imageInSD);

        sourceImage = BitmapFactory.decodeFile(iD, bfo);
        
        FaceDetector faceDet = new FaceDetector(sourceImage.getWidth(), sourceImage.getHeight(), 3);
        FaceDetector.Face[] faceList = new FaceDetector.Face[3];
        faceDet.findFaces(sourceImage, faceList);
		
        for (int i=0; i < faceList.length; i++) {
            Face face = faceList[i];
            Log.d("FaceDet", "Face ["+face+"]");
            if (face != null) {
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                PointF pf = new PointF();
                face.getMidPoint(pf);
                Log.d("FaceDet", "\t Eyes distance ["+face.eyesDistance()+"] - Face midpoint ["+pf+"]");
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
              countfaces++;
            }
        }
        return countfaces;
	}

	public String compressImage(String s) {
		 
        String filePath = s;
        Bitmap scaledBitmap = null;
 
        BitmapFactory.Options options = new BitmapFactory.Options();
 
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
 
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
 
//      max Height and width values of the compressed image is taken as 816x612
 
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
 
//      width and height values are set maintaining the aspect ratio of the image
 
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
 
            }
        }
 
//      setting inSampleSize value allows to load a scaled down version of the original image
 
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
 
//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
 
//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
 
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
 
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
 
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
 
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
 
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
 
//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
 
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
 
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        return filename;
 
    }
	public String getFilename() {
	    File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
	    return uriSting;
	 
	}
	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	 
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height/ (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
	        inSampleSize++;
	    }
	 
	    return inSampleSize;
	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}	
}