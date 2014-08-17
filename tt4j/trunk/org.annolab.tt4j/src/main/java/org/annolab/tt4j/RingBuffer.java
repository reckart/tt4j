/*******************************************************************************
 * Copyright (c) 2009-2014 Richard Eckart de Castilho.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
package org.annolab.tt4j;

public
class RingBuffer
{
	private final String[] _content;
	private int _begin;
	private int _end;

	public
	RingBuffer(
			final int size)
	{
		_content = new String[size];
		_begin = -1;
		_end = 0;
	}

	public
	void add(
			final String token)
	{
		if (_begin == -1) {
			_begin = 0;
			_content[_end] = token;
		}
		else {
			_end++;
			if (_end >= _content.length) {
				_end = 0;
			}
			if (_end == _begin) {
				_begin ++;
				if (_begin >= _content.length) {
					_begin = 0;
				}
			}
			_content[_end] = token;
		}
	}

	public
	int size()
	{
		if (_begin <= _end) {
			return _end - _begin + 1;
		}
		else {
			return _content.length - _begin + _end + 1;
		}
	}
	
	@Override
	public
	String toString()
	{
		if (_begin == -1) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		int i = _begin;
		while (i != _end) {
			sb.append(_content[i]);
			i++;
			if (i >= _content.length) {
				i = 0;
			}
			sb.append(' ');
		}
		sb.append(_content[i]);

		return sb.toString();
	}
}
