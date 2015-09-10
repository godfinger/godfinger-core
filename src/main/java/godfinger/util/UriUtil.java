/*
 * Copyright 2015 Godfinger Framework
 *
 * Godfinger Framework licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package godfinger.util;

public class UriUtil {

	public static String decodePath(String uri) {
		if (uri == null) {
			return null;
		}

		int pathEndPos = uri.indexOf('?');
		if (pathEndPos < 0) {
			return normalizePath(uri);
		} else {
			return normalizePath(uri.substring(0, pathEndPos));
		}
	}

	public static String normalizePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

}
