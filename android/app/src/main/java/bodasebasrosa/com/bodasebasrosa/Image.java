package bodasebasrosa.com.bodasebasrosa;

import java.io.Serializable;

/**
 * Created by dalme on 16/3/18.
 */

public class Image implements Serializable {
    private String src;
    private int width;
    private int height;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}