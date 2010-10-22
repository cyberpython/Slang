/*
 *  Copyright 2010 Georgios Migdos.
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
package tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 *
 * @author cyberpython
 */
public class BatchSVGToPNGExporter {

    public static void SVGToImageIcon(File SVGFile, File outFile, int width, int height) {

        try {
            PNGTranscoder t = new PNGTranscoder();
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
            t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(height));
            String svgURI = SVGFile.toURL().toString();
            TranscoderInput input = new TranscoderInput(svgURI);
            OutputStream ostream = new FileOutputStream(outFile);
            TranscoderOutput output = new TranscoderOutput(ostream);
            t.transcode(input, output);
            ostream.flush();
            ostream.close();
        } catch (IOException ioe) {
        } catch (TranscoderException te) {
            System.err.println(outFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        File dir = new File(args[0]);
        int d = Integer.parseInt(args[1]);

        String[] children = dir.list();
        if (children == null) {
        } else {
            for (int i = 0; i < children.length; i++) {
                String filename = children[i];
            }
        }
        FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".svg");
            }
        };
        children = dir.list(filter);
        
        for (String file : children) {
            File f = new File(dir.getAbsolutePath()+File.separator+file);
            File outFile = new File(dir.getAbsolutePath()+File.separator+file.substring(0, file.length()-4)+".png");
            SVGToImageIcon(f,outFile,d,d);
        }

    }
}
