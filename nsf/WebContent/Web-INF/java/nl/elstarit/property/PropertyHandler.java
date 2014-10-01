package nl.elstarit.property;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;

import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewEntryCollection;
import org.openntf.domino.utils.XSPUtil;
import org.openntf.domino.Document;

import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import eu.linqed.debugtoolbar.DebugToolbar;

import nl.elstarit.bean.Language;
import nl.elstarit.utils.JSFUtil;
import nl.elstarit.utils.OpenLogItem;

public class PropertyHandler implements Serializable {
    private static final long serialVersionUID = 1L;
    private static String bundleVar = "en_basic";
    private static final String BEAN_NAME = "propertyHandler";
    private TreeMap<String,Property> properties = new TreeMap<String,Property>();
    private boolean propertiesLoaded = false;
    
    public PropertyHandler(){
        
    }
    
    public void initiliaze(){
        
        if(!propertiesLoaded){
            loadPropertiesFromView();
        }
    }
    
    public static PropertyHandler get() {
        return (PropertyHandler) JSFUtil.resolveVariable(BEAN_NAME);
    }
    
    public void loadPropertiesFromView(){
        try{
            if(properties != null){
                properties.clear();
            }else{
                properties = new TreeMap<String,Property>();
            }
            View vwProperties = XSPUtil.getCurrentDatabase().getView("properties");
            ViewEntryCollection collection = vwProperties.getAllEntries();
            for(ViewEntry entryProperty: collection){
                Document docProperty = entryProperty.getDocument();
                String key = docProperty.getItemValueString("key");
                //String value = docProperty.getItemValueString("value");
                
                if(!properties.containsKey(key)){
                    addProperty(docProperty);                   
                }
            }
            
            setPropertiesLoaded(true);
        }catch(Exception e){
            OpenLogItem.logError(XSPUtil.getCurrentSession(), e);
        }
    }
    
    public void loadPropertiesFromFile(){
        
        try{ 
            ResourceBundle bundle;
            bundle = JSFUtil.getXSPContext().bundle(bundleVar);
            if (bundle != null) {
                for(String key: bundle.keySet()){
                    
                    String value = bundle.getString(key);
                    // docProperty = vwProperties.getDocumentByKey(key, true);

                    if(!properties.containsKey(key)){
                        Document docProperty = XSPUtil.getCurrentDatabase().createDocument();
                        docProperty.replaceItemValue("Form","property");
                        docProperty.replaceItemValue("key", key);
                        docProperty.replaceItemValue("value", value);
                        
                        docProperty.save();
                        
                        addProperty(docProperty);
                    }
                    
                }
                XSPUtil.getCurrentDatabase().getView("properties").refresh();
            }else{
                JSFUtil.addMessage(FacesMessage.SEVERITY_INFO, Language.getLanguageString("MESSAGE_PROPERTY_FILE_NOT_FOUND"));
            }
        }catch(Exception e){
            //OpenLogItem.logError(XSPUtil.getCurrentSession(), e);
        }
    }
    
    public void addProperty(Document docProperty){
        String key = docProperty.getItemValueString("key");
        if(!properties.containsKey(key)){
            String value = docProperty.getItemValueString("value");
            Property property = new Property();
            property.setLabel(key);
            property.setValue(value);
            properties.put(key, property);
        }
    }
    
    public void updateProperty(DominoDocument dominodocument){
        Document docProperty = XSPUtil.wrap(dominodocument.getDocument());
        String key = docProperty.getItemValueString("key");
        if(properties.containsKey(key)){
            String value = docProperty.getItemValueString("value");
            properties.get(key).setValue(value);
        }
    }
    
    public void removeProperty(Document docProperty){
        String key = docProperty.getItemValueString("key");
        if(properties.containsKey(key)){
            properties.remove(key);
        }
    }
    
    public String getPropertyValue(String key){
        if(properties.containsKey(key)){
            return properties.get(key).getValue();
        }
        return "";
    }

    public void setProperties(TreeMap<String,Property> properties) {
        this.properties = properties;
    }

    public TreeMap<String,Property> getProperties() {
        return properties;
    }

    public void setPropertiesLoaded(boolean propertiesLoaded) {
        this.propertiesLoaded = propertiesLoaded;
    }

    public boolean isPropertiesLoaded() {
        return propertiesLoaded;
    }
}
