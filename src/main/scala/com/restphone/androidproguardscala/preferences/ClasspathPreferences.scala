package com.restphone.androidproguardscala.preferences;

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IAdaptable
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.jface.preference.RadioGroupFieldEditor
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.IWorkbenchPropertyPage
import com.restphone.androidproguardscala.ClasspathEntryType._
import com.restphone.androidproguardscala.ClasspathEntryData
import com.restphone.androidproguardscala.JavaProjectData
import org.eclipse.jdt.internal.core.JavaProject

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

class ClasspathPreferences
  extends FieldEditorPreferencePage( FieldEditorPreferencePage.GRID )
  with IWorkbenchPropertyPage {
  var projectData: JavaProjectData = null
  var classpathItemEditors: List[ClasspathEntryFieldEditor] = List.empty

  def setElement( e: IAdaptable ) = {
    projectData = new JavaProjectData( JavaCore.create (e.asInstanceOf[IProject]))
    setPreferenceStore( projectData.preferences )
  }

  override def getElement = projectData.getProject

  setDescription( "Choose the jar files that will be included in the shrunken final jar.  Input jars are included, library jars are passed to Proguard, and ignored files are ignored." )

  case class ClasspathEntryFieldEditor( displayLabel: String,
                                        fieldName: String,
                                        container: Composite )
    extends RadioGroupFieldEditor( fieldName, displayLabel, 3, ClasspathEntryFieldEditor.choiceValues,
      container ) {
  }
  object ClasspathEntryFieldEditor {
    val choiceValues = Array( Array( "Input Jar", INPUTJAR ), Array( "Library Jar", LIBRARYJAR ), Array( "Ignore", IGNORE ) )
  }

  def createFieldEditorForClasspathItem( c: ClasspathEntryData, container: Composite ) = {
    ClasspathEntryFieldEditor( c.fullPath, c.fieldName, container )
  }

  override def createFieldEditors(): Unit = {
    val xs = projectData.classpathEntries.toList
    classpathItemEditors = xs map { c => createFieldEditorForClasspathItem( c, getFieldEditorParent ) }
    classpathItemEditors foreach { f =>
      projectData.preferences.setDefault( f.fieldName, IGNORE )
      addField( f )
      f.setPreferenceStore( projectData.preferences )
      f.setPage( this );
      f.load();
    }
  }
}