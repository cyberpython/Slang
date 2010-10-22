/*
 *  Μίγδος Γεώργιος <cyberpython@gmail.com>, Δόβα Φιλία <filia_do@hotmail.com>
 */

package glossaeditor.ui.components.charsetselectpane;

import java.nio.charset.Charset;

/**
 *
 * @author Μίγδος Γεώργιος <cyberpython@gmail.com>, Δόβα Φιλία <filia_do@hotmail.com>
 */
public class CharsetWrapper {

    private Charset charset;
    private String name;

    public CharsetWrapper(Charset c){
        this.charset = c;
        if(c.equals(Charset.forName("UTF-8"))){
            this.name = "UTF-8";
        }else if(c.equals(Charset.forName("windows-1253"))){
            this.name = "Windows 1253 (Greek)";
        }else{
            this.name = charset.displayName();
        }
    }

    @Override
    public String toString(){
        return this.name;

    }

    public Charset getCharset(){
        return this.charset;
    }

}
