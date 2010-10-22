/*
 * Copyright 2009 Georgios "cyberpython" Migdos cyberpython@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package glossaeditor.ui.components.cli;


import java.io.IOException;
import java.io.PipedInputStream;
import javax.swing.JTextArea;


class CLIReader extends Thread {

    PipedInputStream pipedInputStream1;
    JTextArea jTextArea1;

    CLIReader(PipedInputStream pi, JTextArea textArea) {
        this.pipedInputStream1 = pi;
        this.jTextArea1 = textArea;
    }

    @Override
    public void run() {
        final byte[] buf = new byte[1024];
        try {
            while (true) {
                final int len = pipedInputStream1.read(buf);
                if (len == -1) {
                    break;
                }
                jTextArea1.append(new String(buf, 0, len));
                jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
            }
        } catch (IOException e) {
        }
    }
}