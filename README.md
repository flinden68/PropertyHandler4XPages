PropertyHandler4XPages
======================

2 Classes to have control of labels in property files in a XPages app

This repository contains 2 classes

Property Class
Container to hold the key and label of the property

PropertyHandler Class
The handler who load properties from file and from a view in a TreeMap
It contains methods to delete, update, add properties to the TreeMap.


The bundle variable in the class to load the required property file, is specified in the faces-config.xml
"<resources>
    <bundle>
      <src>/en_basic.properties</src>
      <var>en_basic</var>
    </bundle>
</resources>"

A view is also necessary to get all the property documents

Not provided is the XPages overview and detail page. I think every XPages Developer should be able to create these and customize it to their needs.
