package lol.clann.object.nbt;

import java.util.List;
import static lol.clann.Clann.plugin;


public abstract class NBTContainer<T> {

    abstract public T getObject();

    protected NBTBase readCustomTag(){
        return readTag();
    }

    abstract protected NBTBase readTag();

    abstract protected void writeTag(NBTBase base);

    protected void writeCustomTag(NBTBase base){
        writeTag(base.clone());
    }

    protected void eraseTag() {
        writeTag(new NBTTagCompound());
    }
    protected void eraseCustomTag() {
        eraseTag();
    }

    abstract protected Class<T> getContainerClass();

    final public String getName(){
        return getContainerClass().getSimpleName();
    }

    public abstract List<String> getTypes();

    // ########################## PowerNBT API ##########################

    /**
     * Set value of container root tag
     * @see #removeTag() remove tag if value is null
     * @param value root tag
     */
    final public void setTag(NBTBase value){
        if(value==null) {
            eraseTag();
            return;
        }
        writeTag(value.clone());
    }

    /**
     * Set value of container root tag using PowerNBT options
     * @see #removeCustomTag() remove tag if value is null
     * @param value root tag
     */
    final public void setCustomTag(NBTBase value){
        if(value==null) {
            eraseCustomTag();
            return;
        }
        value = value.clone();
        if(value instanceof NBTTagCompound){
            NBTTagCompound tag = (NBTTagCompound) value;
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_set."+getName());
            if(ignoreList!=null) for(String ignore:ignoreList) tag.remove(ignore);
        }
        writeCustomTag(value);
    }

    /**
     * Get root tag of container
     * @return NBT tag
     */
    final public NBTBase getTag(){
        return readTag();
    }

    /**
     * Get root tag of container using PowerNBT options
     * @return NBT tag
     */
    final public NBTBase getCustomTag(){
        NBTBase value = readTag();
        if(value instanceof NBTTagCompound){
            NBTTagCompound tag = (NBTTagCompound) value;
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_set."+getName());
            if(ignoreList!=null) for(String ignore:ignoreList) tag.remove(ignore);
        }
        return value;
    }


    /**
     * remove all NBT tags from container or remove contained object
     */
    public final void removeTag(){
        eraseTag();
    }



    /**
     * remove all NBT tags from container or remove contained object using PowerNBT options
     */
    public final void removeCustomTag(){
        eraseCustomTag();
    }

    private NBTBase get(){
        return readTag();
    }

    private void set(Object value){
       writeTag(NBTBase.getByValue(value));
    }

    public String toString(){
        return getName();
    }


}
