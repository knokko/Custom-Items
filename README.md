# Custom-Items
Work in progress plug-in for minecraft that can add custom items with an editor.


This project is work in progress, so the features are not yet complete.
However, there are already quite some things that can be achieved.


Java projects:

This repository has 4 java projects: The API, BasePlugin, Editor and EditorTester.
The API is the code that is used by both BasePlugin and Editor.
The BasePlugin is the code for the actual plug-in.
The Editor is the code for the editor users need to use to create their item sets.
The EditorTester is the code for the automatic tests for the Editor.


Dependencies:

API doesn't rely on any dependencies.

BasePlugin relies on CraftBukkit (obviously), API (the javaproject within this repository), BitHelper (code is on github.com/knokko/BitHelper) and CorePlugin (code is on github.com/knokko/Core-Plugin-1.12.2).

Editor relies on API, BitHelper and Gui (code is on github.com/knokko/Gui).

EditorTester relies on API, BitHelper, Editor, Gui, GuiTester (code is on github.com/knokko/SafeGuiTester) and jnativehook (see instructions on github.com/kwhat/jnativehook).

Currently, I have java projects for all dependencies except jnativehook on my laptop. If you would want to also work on this plug-in, you can download all the code from them to your computer, turn them into a java project and add them to your buildpath. Install jnativehook however you like. (Yeah yeah, I should look into maven or gradle someday instead of keeping up this dirty business...)

Exporting:

To export the plug-in, create a jar file containing the sources of both API and BasePlugin (notice that the plugin.yml is in the source folder of BasePlugin).

To export the editor, export it as a runnable jar file (your java editor should automatically export all dependencies into that same jar).

I never export EditorTester because I just run it directly from my java editor.
