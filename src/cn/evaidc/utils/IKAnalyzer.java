package cn.evaidc.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.IOUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IKAnalyzer extends Analyzer{  
    
    private boolean useSmart;  
      
    public boolean useSmart() {  
        return useSmart;  
    }  
  
    public void setUseSmart(boolean useSmart) {  
        this.useSmart = useSmart;  
    }  
  
    /** 
     * IK�ִ���Lucene  Analyzer�ӿ�ʵ���� 
     *  
     * Ĭ��ϸ�����з��㷨 
     */  
    public IKAnalyzer(){  
        this(false);  
    }  
      
    /** 
     * IK�ִ���Lucene Analyzer�ӿ�ʵ���� 
     *  
     * @param useSmart ��Ϊtrueʱ���ִ������������з� 
     */  
    public IKAnalyzer(boolean useSmart){  
        super();  
        this.useSmart = useSmart;  
    }  
  
  
    @Override  
    protected TokenStreamComponents createComponents(String fieldName) {  
         Reader reader=null;  
            try{  
                reader=new StringReader(fieldName);  
                IKTokenizer it = new IKTokenizer(reader);  
                return new Analyzer.TokenStreamComponents(it);  
            }finally {  
                IOUtils.closeWhileHandlingException(reader);  
            }  
    }  
  
}

class IKTokenizer extends Tokenizer {  
  
    // IK�ִ���ʵ��  
    private IKSegmenter _IKImplement;  
  
    // ��Ԫ�ı�����  
    private final CharTermAttribute termAtt;  
    // ��Ԫλ������  
    private final OffsetAttribute offsetAtt;  
    // ��Ԫ�������ԣ������Է���ο�org.wltea.analyzer.core.Lexeme�еķ��ೣ����  
    private final TypeAttribute typeAtt;  
    // ��¼���һ����Ԫ�Ľ���λ��  
    private int endPosition;  
  
    public IKTokenizer(Reader in) {  
        this(in, false);  
    }  
  
    /** 
     * Lucene 6.5.0 Tokenizer�������๹�캯�� 
     *  
     * @param in 
     * @param useSmart 
     */  
    public IKTokenizer(Reader in, boolean useSmart) {  
        offsetAtt = addAttribute(OffsetAttribute.class);  
        termAtt = addAttribute(CharTermAttribute.class);  
        typeAtt = addAttribute(TypeAttribute.class);  
        _IKImplement = new IKSegmenter(input, useSmart);  
    }  
  
    /* 
     * (non-Javadoc) 
     *  
     * @see org.apache.lucene.analysis.TokenStream#incrementToken() 
     */  
    @Override  
    public boolean incrementToken() throws IOException {  
        // ������еĴ�Ԫ����  
        clearAttributes();  
        Lexeme nextLexeme = _IKImplement.next();  
        if (nextLexeme != null) {  
            // ��Lexemeת��Attributes  
            // ���ô�Ԫ�ı�  
            termAtt.append(nextLexeme.getLexemeText());  
            // ���ô�Ԫ����  
            termAtt.setLength(nextLexeme.getLength());  
            // ���ô�Ԫλ��  
            offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());  
            // ��¼�ִʵ����λ��  
            endPosition = nextLexeme.getEndPosition();  
            // ��¼��Ԫ����  
            typeAtt.setType(nextLexeme.getLexemeTypeString());  
            // ����true��֪�����¸���Ԫ  
            return true;  
        }  
        // ����false��֪��Ԫ������  
        return false;  
    }  
  
    /* 
     * (non-Javadoc) 
     *  
     * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader) 
     */  
    @Override  
    public void reset() throws IOException {  
        super.reset();  
        _IKImplement.reset(input);  
    }  
  
    @Override  
    public final void end() {  
        // set final offset  
        int finalOffset = correctOffset(this.endPosition);  
        offsetAtt.setOffset(finalOffset, finalOffset);  
    }  
}  