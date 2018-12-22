/*
 * @(#)cookies.js 0.1  2003-02-05
 *
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 *
 *
 * 
 * The following conditions apply only, if this software is distributed 
 * as part of TinyLMS:
 *
 *      This program is free software; you can redistribute it and/or modify it 
 *      under the terms of the GNU General Public License as published by the 
 *      Free Software  Foundation; either version 2 of the License, or (at your
 *      option) any later version. 
 *
 *      This program is distributed in the hope that it will be useful, but 
 *      WITHOUT ANY WARRANTY; without even the implied warranty of 
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 *      Public License for more details. You should have received a copy of the
 *      GNU General Public License along with this program; if not, write to the
 *      Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *      02111-1307 USA
 */

/**
 * This file contains functions for cookie access.
 *
 * This file is intended to be included in the head of a HTML document.
 *
 * Example:
 * <html>
 *   <head>
 *     <title>Learning Management System</title>
 *     <script language="JavaScript" src="listlogger.js" type="text/JavaScript"></script>
 *     <script language="JavaScript" src="collections.js" type="text/JavaScript"></script>
 *     <script language="JavaScript" src="cookies.js" type="text/JavaScript"></script>
 *   </head>
 * </html>
 *
 */
/**
 * Sets cookie values for the specified document. Expiration date is optional.
 * @param doc A document.
 * @param name A String.
 * @param value A String.
 * @param expire A Date.
 */
function setCookieForDocument(doc, name, value, expire) {
  	logger.write(logger.INTERNAL,"cookies.js setCookieForDocument("+doc+","+name+","+value+","+expire+")");
  doc.cookie = name + "=" + escape(value)
  + ((expire == null) ? "" : ("; expires=" + expire.toGMTString()))

}
/**
 * Sets cookie values for the current document. Expiration date is optional.
 * @param name A String.
 * @param value A String.
 * @param expire A Date.
 */
function setCookie(name, value, expire) {
  setCookieForDocument(document, name, value, expire);
}

/**
 * Gets a cookie value from the specified document, given the name of the cookie.
 *
 * @param doc A document.
 * @param name A String. 
 * @return Returns the value of the cookie or null, if the cookie was not found.
 */
function getCookieFromDocument(doc, name) {
  logger.write(logger.INTERNAL,"cookies.js getCookieFromDocument("+doc+","+name+")");
  var search = name + "="
  if (doc.cookie.length > 0) {
		// if there are any cookies
    offset = doc.cookie.indexOf(search)
    if (offset != -1) {
		  // if cookie exists
      offset += search.length

      // set index of beginning of value
      end = doc.cookie.indexOf(";", offset)

      // set index of end of cookie value
      if (end == -1)
        end = doc.cookie.length;
      var value = unescape(doc.cookie.substring(offset, end));
  	logger.write(logger.INTERNAL_SUCCESS,"cookies.js getCookieFromDocument("+doc+","+name+") value="+value);
	  return value;
    }
  }
  	logger.write(logger.INTERNAL_FAILURE,"cookies.js getCookieFromDocument("+doc+","+name+") no cookie found!");
  return null;
}

/**
 * Gets a cookie value from the current document, given the name of the cookie.
 * @param name A String.
 * @return Returns the value of the cookie or null, if the cookie was not found.
 */
function getCookie(name) {
  logger.write(logger.INTERNAL,"cookies.js getCookie("+name+")");
  return getCookieFromDocument(document, name);
}

