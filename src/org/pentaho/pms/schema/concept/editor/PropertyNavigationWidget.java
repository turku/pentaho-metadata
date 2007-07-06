package org.pentaho.pms.schema.concept.editor;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.DefaultPropertyID;

public class PropertyNavigationWidget extends Composite implements ISelectionProvider {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(PropertyNavigationWidget.class);

  // ~ Instance fields =================================================================================================

  private PropertyTreeWidget propertyTree;

  private ToolItem delButton;

  private IConceptModel conceptModel;

  private EventSupport eventSupport = new EventSupport();

  // ~ Constructors ====================================================================================================

  public PropertyNavigationWidget(final Composite parent, final int style, final IConceptModel conceptModel) {
    super(parent, style);
    this.conceptModel = conceptModel;
    createContents();
  }

  // ~ Methods =========================================================================================================

  protected void createContents() {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PropertyNavigationWidget.this.widgetDisposed(e);
      }
    });

    setLayout(new GridLayout(2, false));

    Label lab1 = new Label(this, SWT.NONE);
    lab1.setText("Available");

    ToolBar tb3 = new ToolBar(this, SWT.FLAT);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalAlignment = SWT.END;
    tb3.setLayoutData(gridData);

    ToolItem ti4 = new ToolItem(tb3, SWT.PUSH);
    ti4.setImage(Constants.getImageRegistry(Display.getCurrent()).get("add-button"));
    ti4.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(final SelectionEvent e) {
        PropertyNavigationWidget.this.addButtonPressed(e);
      }

      public void widgetSelected(final SelectionEvent e) {
        PropertyNavigationWidget.this.addButtonPressed(e);
      }

    });
    delButton = new ToolItem(tb3, SWT.PUSH);
    delButton.setImage(Constants.getImageRegistry(Display.getCurrent()).get("del-button"));
    delButton.setEnabled(false);
    delButton.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(final SelectionEvent e) {
        PropertyNavigationWidget.this.deleteButtonPressed(e);
      }

      public void widgetSelected(final SelectionEvent e) {
        PropertyNavigationWidget.this.deleteButtonPressed(e);
      }
    });

    propertyTree = new PropertyTreeWidget(this, SWT.NONE, conceptModel, PropertyTreeWidget.SHOW_USED, true);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.horizontalSpan = 2;
    propertyTree.setLayoutData(gridData);

    propertyTree.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(final SelectionChangedEvent e) {
        if (e.getSelection() instanceof PropertyTreeSelection) {
          fireSelectionChangedEvent(e);
          PropertyTreeSelection sel = (PropertyTreeSelection) e.getSelection();
          if (sel.isGroup()) {
            // we're dealing with a property group
            delButton.setEnabled(false);
          } else {
            // we're dealing with a property
            ConceptPropertyInterface prop = conceptModel.getProperty(sel.getName(), IConceptModel.REL_THIS);
            if (null != prop) {
              // it's a child property; allow it to be deleted
              if (prop.isRequired()) {
                delButton.setEnabled(false);
              } else {
              delButton.setEnabled(true);
              }
            } else {
              // it's a parent/inherited/security property; it cannot be deleted on this concept
              delButton.setEnabled(false);
            }
          }
        } else {
          delButton.setEnabled(false);
        }
      }
    });
  }

  protected void deleteButtonPressed(final SelectionEvent e) {
    String propertyId = null;
    ISelection sel = propertyTree.getSelection();
    if (sel instanceof PropertyTreeSelection) {
      PropertyTreeSelection treeSel = (PropertyTreeSelection) sel;
      propertyId = treeSel.getName();

      // event should now fire from the model
    } else {
      if (logger.isWarnEnabled()) {
        logger.warn("unknown node selected in property tree");
      }
      return;
    }

    boolean delete = MessageDialog.openConfirm(getShell(), "Confirm", "Are you sure you want to remove the property '"
        + PredefinedVsCustomPropertyHelper.getDescription(propertyId) + "'?");

    if (delete) {
      conceptModel.removeProperty(propertyId);
    }
  }

  protected void widgetDisposed(final DisposeEvent e) {

  }

  protected void addButtonPressed(final SelectionEvent e) {
    new AddPropertyDialog(getShell(), conceptModel).open();
  }

  public void addSelectionChangedListener(final ISelectionChangedListener listener) {
    eventSupport.addListener(listener);
  }

  public ISelection getSelection() {
    return propertyTree.getSelection();
  }

  public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
    eventSupport.removeListener(listener);
  }

  protected void fireSelectionChangedEvent(final SelectionChangedEvent e) {
    Set listeners = eventSupport.getListeners();
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
      ISelectionChangedListener listener = (ISelectionChangedListener) iter.next();
      listener.selectionChanged(e);
    }
  }

  public void setSelection(ISelection selection) {
    //  not currently supported
    throw new UnsupportedOperationException();
  }
}
