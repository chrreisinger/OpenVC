type dummyAccess is access integer;
procedure showMessage(unused : dummyAccess; msg: string);
attribute foreign of showMessage [dummyAccess, string] :
  procedure is "javax/swing/JOptionPane showMessageDialog (Ljava/awt/Component;Ljava/lang/Object;)V";
