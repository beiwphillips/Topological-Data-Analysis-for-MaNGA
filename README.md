# USING CONTOUR TREES FOR MANGA DATA CUBE DENOISING

This program is developed for a project of my thesis for the MS in Computing at the University of Utah. In this project, we use contour tree (CT), a topological descriptor in TDA, to remove the noise from data cubes that arise from the Mapping Nearby Galaxies at Apache Point Observatory (MaNGA) project of the Sloan Digital Sky Survey (SDSS). The program is designed to run contour tree simplification over a MaNGA data cube.

This project is supported by Carnegie Institution for Science for a Carnegie Science Venture Grant on Extracting the Full Information Content of Astrophysical Data Cubes. The project is joint work with Juna A. Kollmeier, Paul Rosen, Anil Seth, Joel Brownstein, Guillermo Blanc, Nicholas Fraser Boardman, Gail Zasowski, and Bei Wang. 

## Prerequisites
1. In order to run the program, please make sure you have Java SE Development Kit 8 installed on your computer. If not, please follow the instructions on this website https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

2. Please also refer to the MaNGA website https://www.sdss.org/surveys/manga/ for astronomical knowledge training, and download the public available data from the MaNGA survey.

## Run
1. Please clone or download the whole project and build with JDK. You can either use commmand line or your favorite IDEs, such as Eclipse https://www.eclipse.org/downloads/. 

2. Run the program with the following configurations:

* dim: 2D is prefered, 3D is also available but not favorable

* x=[x1,x2], x1 and x2 are the range of the first dimension to run simplification on.

* y=[y1,y2], y1 and y2 are the range of the second dimension to run simplification on.

* z=[z1,z2], z1 and z2 are the range of the second dimension to run simplification on.

* simplify, a decimal number from 0 to 1 indicating the simplification strenghth.

* metric, choose from "persistence", "volume", or "hypervolume".

* output, the path for outputting the simplified data cube

* the path of the cube to run simplification on.

**example** 

1. dim=2D x=[0,73] y=[0,73] z=[1045,1047] simplify=0.8 metric=persistence output=/Users/yulong/manga/test_persistence.fits /Users/yulong/manga/test.fits

2. dim=3D x=[0,73] y=[0,73] z=[0,4056] simplify=0.05 metric=volume output=/Users/yulong/manga/test_volume.fits /Users/yulong/manga/test.fits

## Evaluate tools
1. QFitsView http://www.mpe.mpg.de/~ott/QFitsView/
2. Astropy, a Python package https://www.astropy.org/

## Contact
If you have any questions, please feel free to ping me.
