/*
 *     ALMA TDA - Contour tree based simplification and visualization for ALMA
 *     data cubes.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.common.data.cache;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class BaseCache<PageType extends BasePage> {
	
	public static int PAGE_1K  = 1024;
	public static int PAGE_2K  = 2048;
	public static int PAGE_4K  = 4096;
	public static int PAGE_8K  = 8192;
	public static int PAGE_16K = 16384;
	public static int PAGE_32K = 32768;

	protected boolean					 verbose;
	protected boolean					 read_only;
	protected FileChannel   			 file_chan;
	protected int 				  		 page_count;
	protected int 					     page_size;
	protected Path						 cache_file;
	
	
	public BaseCache( String file, int pg_size, int pg_count, boolean read_only, boolean verbose ) throws IOException {
		this.verbose 	 = verbose;
		this.cache_file  = FileSystems.getDefault().getPath(file);
		this.read_only   = read_only;
		this.page_size   = pg_size; 
		this.page_count  = pg_count;
		
		if( read_only )
			file_chan = FileChannel.open( cache_file, StandardOpenOption.READ );
		else
			file_chan = FileChannel.open( cache_file, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );
	}
	

	public abstract void writeBackAll() throws IOException ;
	
	public final void close( boolean delete ) throws IOException {
		if( file_chan != null ){
			System.out.printf("closing cache!\n");
			writeBackAll();
			file_chan.close();
			file_chan = null;
			if( delete ){
				try {
				    Files.delete(cache_file);
				} catch (NoSuchFileException x) {
				    System.err.format("%s: no such" + " file or directory%n", cache_file);
				}
			}
		}
	}
	
	public abstract void PrintPageInfo( );
	

	public final void PrintCacheInfo( ){
		System.out.printf("Cache Info\n");
		System.out.printf("  Estimated data size: %dM\n", GetDataSize()/1024/1024);
		System.out.printf("  Current pages:       %d of %d\n",GetCurrentPageCount(),page_count);
		System.out.printf("  Bytes per page:      %d\n",page_size);
	}

	
	public abstract int  GetCurrentPageCount( );
	
	protected abstract PageType getPage( long page_id ) throws IOException;

	
	public final long GetCurrentDataSize() {
		return GetCurrentPageCount()*page_size;
	}
	
	public final long GetDataSize(){
		return page_count*page_size;
	}
	
	public final long GetFileSize() throws IOException {
		return file_chan.size();
	}

	public final int GetPageCount( ){
		return page_count;
	}
	
	
}
