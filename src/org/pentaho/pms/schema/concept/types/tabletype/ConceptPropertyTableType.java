/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.schema.concept.types.tabletype;

import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.ConceptPropertyBase;
import org.pentaho.pms.schema.concept.types.ConceptPropertyType;

public class ConceptPropertyTableType extends ConceptPropertyBase implements ConceptPropertyInterface, Cloneable
{
    public static final ConceptPropertyTableType DEFAULT_OTHER     = new ConceptPropertyTableType("tabletype", TableTypeSettings.OTHER);    //$NON-NLS-1$
    public static final ConceptPropertyTableType DEFAULT_DIMENSION = new ConceptPropertyTableType("tabletype", TableTypeSettings.DIMENSION);    //$NON-NLS-1$
    public static final ConceptPropertyTableType DEFAULT_FACT      = new ConceptPropertyTableType("tabletype", TableTypeSettings.FACT);    //$NON-NLS-1$

    private TableTypeSettings value;
    
    public ConceptPropertyTableType(String name, TableTypeSettings value)
    {
        super(name);
        this.value = value;
    }

    public String toString()
    {
        return value.toString();
    }

    public Object clone() throws CloneNotSupportedException 
    {
      ConceptPropertyTableType rtn = (ConceptPropertyTableType) super.clone();
      if (value != null) {
        rtn.value = new TableTypeSettings(value.getType());
      }
      return rtn;
    }

    public ConceptPropertyType getType()
    {
        return ConceptPropertyType.TABLETYPE;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = (TableTypeSettings) value;
    }
    
    public boolean equals(Object obj)
    {
        return value.equals(obj);
    }

    public int hashCode()
    {
        return value.hashCode();
    }
}