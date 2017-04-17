package org.marceloleite.projetoanna.bluetooth.btpackage;

import org.marceloleite.projetoanna.bluetooth.btpackage.content.Content;

/**
 * Created by Marcelo Leite on 17/04/2017.
 */

public class Package {

    private static final int PACKAGE_HEADER = 0x427103f0;

    private static final int PACKAGE_TRAILER = 0x04c22892;

    private int id;

    private TypeCode typeCode;

    Content content;

    public Package(int id, TypeCode typeCode, Content content) {
        this.id = id;
        this.typeCode = typeCode;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeCode getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(TypeCode typeCode) {
        this.typeCode = typeCode;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
