# Custom-Items
Work in progress plug-in for minecraft that can add custom items with an editor.


This project is work in progress, so the features are not yet complete.
However, there are already quite some things that can be achieved.

# The magic
#### The textures
If you are also a programmer, you are probably curious how it is possible to add items with new textures without losing existing textures. This plug-in (ab)uses resourcepack predicates for that. Resourcepack predicates allow items to have different textures depending on their durability loss and unbreakable flag. Custom items are 'unbreakable' items that have 'lost' durability. Since such items can't be obtained without commands or plug-ins, it is (usually) not a problem to claim their textures for custom items. If you want to see how those resource packs are generated, you can take a look at the (very dirty) code of Editor/src/nl/knokko/customitems/editor/set/ItemSet.java

#### The stacking of custom items
Unfortunately, only tools can be unbreakable and lose durability, so it was a bit nasty to let custom items stack. I couldn't find a nice way to do it, so the CustomItemsEventHandler listens for InventoryClickEvent to let custom items stack 'manually'...


# Java projects:

This repository has 4 java projects: The SharedCode, BasePlugin, Editor and EditorTester.

The SharedCode is the code that is used by both BasePlugin and Editor.

The BasePlugin is the code for the actual plug-in.

The Editor is the code for the editor users need to use to create their item sets.

The EditorTester is the code for the automatic tests for the Editor.


# Dependencies:

### SharedCode
SharedCode only relies on BitHelper (code is on github.com/knokko/BitHelper).

### BasePlugin
BasePlugin relies on CraftBukkit (obviously), SharedCode (the javaproject within this repository), BitHelper and CorePlugin (code is on github.com/knokko/Core-Plugin-1.12.2). 
#### Optional dependencies
It also has Crazy Enchantments as an optional dependency (you can download its plug-in jar, the same one as you would put in your plug-ins folder, and add that to your build path). Adding that to your buildpath is necessary to have proper support for the hellforged enchantment of that plug-in. If you don't need that support, you can leave it out and ignore all compile errors it gives. The multisupport is made such that the classes that import anything of it will only be loaded if Crazy Enchantments is actually installed on the server (reflection is used to accomplish it).

### Editor
Editor relies on , BitHelper and Gui (code is on github.com/knokko/Gui).

### EditorTester
EditorTester relies on SharedCode, BitHelper, Editor, Gui, GuiTester (code is on github.com/knokko/SafeGuiTester) and jnativehook (see instructions on github.com/kwhat/jnativehook).

Currently, I have java projects for all dependencies except jnativehook on my laptop. If you would want to also work on this plug-in, you can download all the code from them to your computer, turn them into a java project and add them to your buildpath. Install jnativehook however you like. (Yeah yeah, I should look into maven or gradle someday instead of keeping up this dirty business...)

# Exporting:

To export the plug-in, create a jar file containing the sources of both SharedCode and BasePlugin (notice that the plugin.yml is in the source folder of BasePlugin).

To export the editor, export it as a runnable jar file (your java editor should automatically export all dependencies into that same jar).

I never export EditorTester because I just run it directly from my java editor.
