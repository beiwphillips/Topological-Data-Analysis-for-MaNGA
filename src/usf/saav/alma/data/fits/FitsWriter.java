package usf.saav.alma.data.fits;

import java.io.File;
import java.io.IOException;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.FitsUtil;
import nom.tam.fits.ImageHDU;
import nom.tam.fits.common.FitsException;
import nom.tam.util.BufferedFile;
import usf.saav.scalarfield.ScalarField2D;

public class FitsWriter {

	// slice dims
	final int SLICEWIDTH;
	final int SLICEHEIGHT;
	
	// channels
	final int DEPTH;
	final int WORDSIZE = 4;

	float [][][] data;
	
	BufferedFile bf;
    Fits fits;

	public FitsWriter( int w, int h, int d ){
		SLICEWIDTH = w;
		SLICEHEIGHT = h;
		DEPTH = d;
//		data = new float[1][SLICEHEIGHT][SLICEWIDTH];
	}
	
	public void open( FitsReader fitsReader, String outfile ) throws IOException, FitsException{
	    File file = fitsReader.getFile();
	    fits = new Fits(file);
	    
	    try {
	        while (true) {
	            BasicHDU<?> hdu = fits.readHDU();
	            if (hdu.getKernel() != null) {
	                data = (float[][][]) hdu.getKernel();
	                break;
	            }
	        }
        } catch (Exception e) {
            // TODO: handle exception
        }
	    
//	    ImageHDU iHdu = (ImageHDU) fits.getHDU(1);
//	    data = (float[][][]) iHdu.getKernel();
	    
//		String originalFilename = file.getName();
//		String comment = "Propagated from " + originalFilename;
//		double [] coordOrigin = fitsReader.getCoordOrigin();
//		double [] coordDelta = fitsReader.getCoordDelta();
//
			bf = new BufferedFile(outfile, "rw", 16384);
//
//			ImageHDU ihdu = (ImageHDU) FitsFactory.hduFactory(data);
//			ihdu.getHeader().addValue("NAXIS3", DEPTH, "Actual number of channels");
//
//			// propagate header info from RawFitsReader
//			// Note: assuming first reader was the one that read the displayed data cube.
//			// It's probably a reasonable assumption...
//			for(int i = 0; i < coordOrigin.length; i++) {
//				ihdu.getHeader().addValue("CRVAL"+(i+1), coordOrigin[i], comment);
//			}
//
//			for(int i = 0; i < coordDelta.length; i++) {
//				ihdu.getHeader().addValue("CDELT"+(i+1), coordDelta[i], comment);
//			}
//
//			ihdu.getHeader().write(bf);

		
	}
	
	public void writeSlice( ScalarField2D sf ) throws IOException{
		// write out data cube by channel
		for (int w = 0; w < SLICEWIDTH; ++w) {
			for (int h = 0; h < SLICEHEIGHT; ++h) {
				data[0][h][w] = sf.getValue(w, h);
			}
		}
		bf.writeArray(data);
	}
	
   public void writeSlice( ScalarField2D sf, int d ) throws IOException{
        // write out data cube by channel
        for (int w = 0; w < SLICEWIDTH; ++w) {
            for (int h = 0; h < SLICEHEIGHT; ++h) {
                data[d][h][w] = sf.getValue(w, h);
            }
        }
    }

	public void close( ) throws FitsException, IOException{
//	   FitsUtil.pad(bf, SLICEWIDTH*SLICEHEIGHT*DEPTH*WORDSIZE);
	   fits.write(bf);
       try {
            while (true) {
                BasicHDU<?> hdu = fits.readHDU();
                hdu.write(bf);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
		bf.close();
	}
	
}

