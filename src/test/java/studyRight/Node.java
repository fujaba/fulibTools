package studyRight;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class Node
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_PARENT = "parent";
   public static final String PROPERTY_CHILDREN = "children";

   private String id;
   private Node parent;
   private List<Node> children;

   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Node setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public Node getParent()
   {
      return this.parent;
   }

   public Node setParent(Node value)
   {
      if (this.parent == value)
      {
         return this;
      }

      final Node oldValue = this.parent;
      if (this.parent != null)
      {
         this.parent = null;
         oldValue.withoutChildren(this);
      }
      this.parent = value;
      if (value != null)
      {
         value.withChildren(this);
      }
      this.firePropertyChange(PROPERTY_PARENT, oldValue, value);
      return this;
   }

   public List<Node> getChildren()
   {
      return this.children != null ? Collections.unmodifiableList(this.children) : Collections.emptyList();
   }

   public Node withChildren(Node value)
   {
      if (this.children == null)
      {
         this.children = new ArrayList<>();
      }
      if (!this.children.contains(value))
      {
         this.children.add(value);
         value.setParent(this);
         this.firePropertyChange(PROPERTY_CHILDREN, null, value);
      }
      return this;
   }

   public Node withChildren(Node... value)
   {
      for (final Node item : value)
      {
         this.withChildren(item);
      }
      return this;
   }

   public Node withChildren(Collection<? extends Node> value)
   {
      for (final Node item : value)
      {
         this.withChildren(item);
      }
      return this;
   }

   public Node withoutChildren(Node value)
   {
      if (this.children != null && this.children.remove(value))
      {
         value.setParent(null);
         this.firePropertyChange(PROPERTY_CHILDREN, value, null);
      }
      return this;
   }

   public Node withoutChildren(Node... value)
   {
      for (final Node item : value)
      {
         this.withoutChildren(item);
      }
      return this;
   }

   public Node withoutChildren(Collection<? extends Node> value)
   {
      for (final Node item : value)
      {
         this.withoutChildren(item);
      }
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public void removeYou()
   {
      this.setParent(null);
      this.withoutChildren(new ArrayList<>(this.getChildren()));
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      return result.substring(1);
   }
}
