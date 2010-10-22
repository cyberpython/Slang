/*
 *  Copyright 2010 cyberpython.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package glossaeditor.integration.iconlocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 *
 * @author cyberpython
 */
public class SVGTranscoder {

    public static ImageIcon SVGToImageIcon(URL SVGFileURL, int width, int height) {

        try {
            PNGTranscoder t = new PNGTranscoder();
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
            t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(height));
            String svgURI = SVGFileURL.toString();
            TranscoderInput input = new TranscoderInput(svgURI);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(ostream);
            t.transcode(input, output);
            ostream.flush();
            ImageIcon m = new ImageIcon(ostream.toByteArray());
            ostream.close();

            return m;
        } catch (IOException ioe) {
        } catch (TranscoderException te) {
        }
        return null;
    }
}
