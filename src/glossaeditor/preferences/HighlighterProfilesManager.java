/*
 *  Copyright 2009 Georgios "cyberpython" Migdos <cyberpython@gmail.com>.
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
package glossaeditor.preferences;

import glossaeditor.Slang;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.DefaultComboBoxModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class HighlighterProfilesManager extends DefaultComboBoxModel {

    private HighlighterProfile defaultProfile;
    private HighlighterProfile currentProfile;

    public HighlighterProfilesManager(HighlighterProfile currentProfile) {
        super();

        this.currentProfile = currentProfile;

        this.defaultProfile = new HighlighterProfile();

        this.addElement(this.currentProfile);
        this.addElement(this.defaultProfile);

        this.setSelectedItem(this.currentProfile);

        String path = "resources/highlighter_profiles.xml";        
        this.loadProfiles( Slang.class.getResourceAsStream(path) );
        
    }

    public HighlighterProfile getCurrentProfile() {
        return this.currentProfile;
    }

    public void setCurrentProfile(HighlighterProfile profile) {
        this.currentProfile = profile;
    }

    public final void loadProfiles(InputStream xmlStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlStream);

            NodeList profiles = document.getElementsByTagName("profile");

            for (int i = 0; i < profiles.getLength(); i++) {
                NodeList profile = profiles.item(i).getChildNodes();
                String[] colors = new String[9];
                String title="";
                for(int j=0; j< profile.getLength(); j++){
                    Node n = profile.item(j);
                    String value = "";
                    if((n.getNodeType() == n.ELEMENT_NODE)&&(n.hasChildNodes())){
                        Node child = n.getFirstChild();
                        value = child.getNodeValue();
                    }
                    String nodeName = n.getNodeName();
                    if(nodeName.equals("title")){
                        title = value;
                    }
                    else if(nodeName.equals("keywords")){
                        colors[0] = value;
                        colors[5] = value;
                    }
                    else if(nodeName.equals("numbers")){
                        colors[1] = value;
                    }
                    else if(nodeName.equals("strings")){
                        colors[2] = value;
                    }
                    else if(nodeName.equals("operators")){
                        colors[3] = value;
                    }
                    else if(nodeName.equals("comments")){
                        colors[4] = value;
                    }
                    else if(nodeName.equals("other")){
                        colors[6] = value;
                        colors[7] = value;
                    }
                    else if(nodeName.equals("background")){
                        colors[8] = value;
                    }
                }
                this.addElement(new HighlighterProfile(title, colors));
            }

        } catch (ParserConfigurationException pce) {
        } catch (SAXException saxe) {
        } catch (IOException ioe) {
        }

    }
}
