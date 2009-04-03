/*
 * Copyright (C) 2007 Lindsay S. Kay, All rights Reserved.
 *
 * This software is provided "as-is", without any express or implied warranty. In no event will the 
 * author be held liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial 
 * applications, and to alter it and redistribute if freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented: you must not claim that you wrote 
 * 	the original software. if you use this software in a product, an acknowledgement in the product 
 * 	documentation would be appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be misrepresented 
 * 	as the original software.
 * 
  * 3. This notice must not be removed or altered from any source distribution.
 */
package com.neocoders.jandal.core;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;

/**
 * 
 * Wrapper for a stream through which a file is being uploaded to a
 * {@link Controller} as a parameter of a client event. Provides attributes such
 * as the original filename in the client's file system, MIME type and file size
 * in bytes. It also provides an {@link InputStream} from which the file can be
 * read, allows the file to be got as a byte array, and can write the file to a
 * file.
 * 
 */
public interface Upload {

    /**
     * Returns the original filename in the client's filesystem, as provided by
     * the browser (or other client software), with no path information.
     * 
     */
    public String getClientFileName();

    /**
     * Returns a code identifying the file MIME type.
     * The code is determined from the entension of the file name returned by
     * {@link getClientFileName}. <br>
     * <br>
     * <table>
     * <tr>
     * <td>Filename Extension</td
     * <td>MIME Type</td>
     * </tr>
     * <tr>
     * <td>txt</td>
     * <td>text/plain</td>
     * </tr>
     * <tr>
     * <td>txt</td>
     * <td>text/plain</td>
     * </tr>
     * <tr>
     * <td>htm, html</td>
     * <td>image/html</td>
     * </tr>
     * <tr>
     * <td>svg</td>
     * <td>image/svg</td>
     * </tr>
     * <tr>
     * <td>rtx</td>
     * <td>text/richtext</td>
     * </tr>
     * <tr>
     * <td>tsv</td>
     * <td>text/tab-seperated-values</td>
     * </tr>
     * <tr>
     * <td>ps</td>
     * <td>application/x-postscript</td>
     * </tr>
     * <tr>
     * <td>class</td>
     * <td>application/java</td>
     * </tr>
     * <tr>
     * <td>zip</td>
     * <td>application/zip</td>
     * </tr>
     * <tr>
     * <td>gtar,tar</td>
     * <td>image/x-tar</td>
     * </tr>
     * <tr>
     * <td>dvi</td>
     * <td>application/dvi</td>
     * </tr>
     * <tr>
     * <td>bin</td>
     * <td>application/octet-stream</td>
     * </tr>
     * <tr>
     * <td>pdf</td>
     * <td>application/pdf</td>
     * </tr>
     * <tr>
     * <td>ps</td>
     * <td>application/postscript</td>
     * </tr>
     * <tr>
     * <td>eps</td>
     * <td>application/postscript</td>
     * </tr>
     * <tr>
     * <td>ai</td>
     * <td>application/postscript</td>
     * </tr>
     * <tr>
     * <td>exe</td>
     * <td>application/octet-stream</td>
     * </tr>
     * <tr>
     * <td>gz</td>
     * <td>application/x-zip</td>
     * </tr>
     * <tr>
     * <td>z</td>
     * <td>application/x-compress</td>
     * </tr>
     * <tr>
     * <td>jpg, jpeg, jpe</td>
     * <td>image/jpeg</td>
     * </tr>
     * 
     * <tr>
     * <td>gif</td>
     * <td>image/gif</td>
     * </tr>
     * <tr>
     * <td>tif,tiff</td>
     * <td>image/tiff</td>
     * </tr>
     * <tr>
     * <td>png</td>
     * <td>image/png</td>
     * </tr>
     * <tr>
     * <td>jpg,jpeg</td>
     * <td>image/jpeg</td>
     * </tr>
     * <tr>
     * <td>wav</td>
     * <td>audio/x-wav</td>
     * </tr>
     * <tr>
     * <td>qt</td>
     * <td>video/quicktime</td>
     * </tr>
     * <tr>
     * <td>mov</td>
     * <td>video/quicktime</td>
     * </tr>
     * <tr>
     * <td>avi</td>
     * <td>video/x-msvideo</td>
     * </tr>
     * <tr>
     * <td>mpv2</td>
     * <td>video/mpeg2</td>
     * </tr>
     * <tr>
     * <td>wrl</td>
     * <td>video/x-vrml</td>
     * </tr>
     * </table>
     */
    public String getContentType();

    /**
     * Provides a hint as to whether or not the file contents will be read from
     * memory.
     * 
     */
    public boolean isInMemory();

    /**
     * Returns the file size in bytes.
     * 
     */
    public long getSize();

    /**
     * Returns an {@link InputStream} through from which the file may be read.
     * 
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Returns data as a byte array.
     * 
     */
    public byte[] toByteArray();

    /**
     * Writes data to a {@link File}.
     * 
     * @throws Exception
     */
    public void write(File file) throws Exception;
}