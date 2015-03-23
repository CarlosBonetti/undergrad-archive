#ifndef TOOLBOX_H_
#define TOOLBOX_H_

#include <gtkmm.h>
#include "cg/DisplayFile.h"
#include "ui/ObjectsTreeView.h"

class Toolbox : public Gtk::Box {
  protected:
    void init_create_widgets();
    void init_control_widgets();
    void init_object_list_widgets();

  public:
    Toolbox(CG::DisplayFile* dfile);
    void refreshObjectList() { _objectsTreeView.refresh(); }

    // Create widgets
    Gtk::Frame _createFrame;
    Gtk::Box _createBox;
    Gtk::Button _newPoint, _newLine, _newPolygon;

    // Control widgets
    Gtk::Frame _controlFrame;
    Gtk::Box _controlBox, _cBox1, _cBox2, _cBox3;
    Gtk::Button _zoomInBtn, _zoomOutBtn, _leftBtn, _downBtn, _upBtn, _rightBtn,
                _rotateLeftBtn, _rotateRightBtn;

    // Object list widgets
    Gtk::Frame _objectsFrame;
    Gtk::ScrolledWindow _objectsScroll;
    ObjectsTreeView _objectsTreeView;
};

#endif
