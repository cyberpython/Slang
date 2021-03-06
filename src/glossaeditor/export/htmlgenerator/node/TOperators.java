/* This file was generated by SableCC (http://www.sablecc.org/). */

package glossaeditor.export.htmlgenerator.node;

import glossaeditor.export.htmlgenerator.analysis.*;

@SuppressWarnings("nls")
public final class TOperators extends Token
{
    public TOperators(String text)
    {
        setText(text);
    }

    public TOperators(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TOperators(getText(), getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTOperators(this);
    }
}
