package project.property;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.attributes.AttributeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ryan Thomson on 14/01/2017.
 */
public class PAttribute extends Property {

    public static final String NAME = ".Attribute";

    private final PPoolEntry nameEntry;
    private final Property[] properties;

    private final AttributeInfo attribute;

    public PAttribute(final AttributeInfo attribute){
        assert attribute != null;
        this.attribute = attribute;
        this.nameEntry = new PPoolEntry(attribute.getNameIndex());
        this.properties = attribute.getProperties();
    }

    @Override
    public Property[] getChildProperties() {
        if(this.properties == null){
            return new Property[]{ this.nameEntry };
        }
        final List<Property> properties = new ArrayList<>();
        properties.add(this.nameEntry);
        for(final Property property : this.properties){
            properties.add(property);
            Collections.addAll(properties, property.getChildProperties());
        }
        return properties.toArray(new Property[properties.size()]);
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final StringBuilder sb = new StringBuilder();
        sb.append(PAttribute.NAME).append("[").append(this.nameEntry.getContentString(pool));
        if(this.properties != null){
            for(final Property property : this.properties) {
                sb.append(", ").append(property.getContentString(pool));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
