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
package godfinger.http.html;

import java.util.Map;

public class HtmlTemplate {

	private final String fileName;
	private final Map<String, ?> parameters;

	public HtmlTemplate(String fileName, Map<String, ?> parameters) {
		this.fileName = fileName;
		this.parameters = parameters;
	}

	public String getFileName() {
		return fileName;
	}

	public Map<String, ?> getParameters() {
		return parameters;
	}

}
