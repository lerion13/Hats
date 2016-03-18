package hats.client.gui;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.client.FMLClientHandler;
import hats.client.core.HatInfoClient;
import hats.client.core.TickHandlerClient;
import hats.client.gui.GuiTradeMaker;
import hats.common.Hats;
import hats.common.core.CommonProxy;
import hats.common.core.HatHandler;
import hats.common.core.HatInfo;
import hats.common.entity.EntityHat;
import hats.common.packet.PacketPlayerHatSelection;
import hats.common.packet.PacketString;
import ichun.client.gui.GuiSlider;
import ichun.client.gui.ISlider;
import ichun.client.keybind.KeyBind;
import ichun.common.iChunUtil;
import ichun.common.core.network.PacketHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiHatSelection extends GuiScreen implements ISlider {

   public static final ResourceLocation texIcons = new ResourceLocation("hats", "textures/gui/icons.png");
   public static final ResourceLocation texChooser = new ResourceLocation("hats", "textures/gui/hatchooser.png");
   private final int ID_PAGE_LEFT = 1;
   private final int ID_DONE_SELECT = 2;
   private final int ID_PAGE_RIGHT = 3;
   private final int ID_CLOSE = 4;
   private final int ID_NONE = 8;
   private final int ID_HAT_COLOUR_SWAP = 9;
   private final int ID_RANDOM = 10;
   private final int ID_FAVOURITES = 11;
   private final int ID_CATEGORIES = 12;
   private final int ID_PERSONALIZE = 13;
   private final int ID_RELOAD_HATS = 14;
   private final int ID_HELP = 15;
   private final int ID_SEARCH = 16;
   private final int ID_ADD = 17;
   private final int ID_CANCEL = 18;
   private final int ID_RENAME = 19;
   private final int ID_DELETE = 20;
   private final int ID_FAVOURITE = 21;
   private final int ID_SET_KEY = 22;
   private final int ID_SET_FP = 23;
   private final int ID_RESET_SIDE = 24;
   private final int ID_MOB_SLIDER = 25;
   private final int ID_SHOW_HATS = 26;
   private final int ID_MAKE_TRADE = 27;
   private final int ID_ACCEPT_TRADE = 28;
   private final int ID_CATEGORIES_START = 30;
   private final int ID_HAT_START_ID = 600;
   private final int VIEW_HATS = 0;
   private final int VIEW_COLOURIZER = 1;
   private final int VIEW_CATEGORIES = 2;
   private final int VIEW_CATEGORY = 3;
   private String category = "";
   private String currentDisplay;
   private GuiTextField searchBar;
   private String selectedButtonName = "";
   private int favourite;
   private int randoMob;
   private boolean hasClicked = false;
   private boolean confirmed = false;
   private boolean adding = false;
   private boolean invalidFolderName = false;
   private boolean deleting = false;
   private boolean justClickedButton = false;
   private boolean renaming = false;
   private boolean addingToCategory = false;
   private boolean personalizing = false;
   private boolean settingKey = false;
   private boolean enabledSearchBar = false;
   public EntityPlayer player;
   public EntityHat hat;
   public List availableHats;
   public List hatsToShow;
   public List categories;
   public List categoryHats = new ArrayList();
   public List enabledButtons = new ArrayList();
   protected int xSize = 176;
   protected int ySize = 170;
   public float mouseX;
   public float mouseY;
   protected int guiLeft;
   protected int guiTop;
   public int pageNumber;
   public int colourR;
   public int colourG;
   public int colourB;
   public int alpha;
   private String prevHatName;
   private int prevColourR;
   private int prevColourG;
   private int prevColourB;
   private int prevAlpha;
   private HatInfoClient tempInfo;
   public int view;
   public Random rand;
   private static final String[] invalidChars = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
   private static int helpPage = 0;
   private static final ArrayList help = new ArrayList();
   private static final String[] helpInfo1 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo1.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo1.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo1.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo1.desc4"), StatCollector.translateToLocal("hats.gui.helpInfo1.desc5")};
   private static final String[] helpInfo2 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo2.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo2.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo2.desc3")};
   private static final String[] helpInfo3 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo3.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo3.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo3.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo3.desc4")};
   private static final String[] helpInfo4 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo4.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo4.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo4.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo4.desc4"), StatCollector.translateToLocal("hats.gui.helpInfo4.desc5")};
   private static final String[] helpInfo5 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo5.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo5.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo5.desc3")};
   private static final String[] helpInfo6 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo6.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo6.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo6.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo6.desc4")};
   private static final String[] helpInfo7 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo7.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo7.desc2")};
   private static final String[] helpInfo8 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo8.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo8.desc2")};
   private static final String[] helpInfo9 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo9.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo9.desc2")};
   private static final String[] helpInfo10 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo10.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo10.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo10.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo10.desc4"), StatCollector.translateToLocal("hats.gui.helpInfo10.desc5")};
   private static final String[] helpInfo11 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo11.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo11.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo11.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo11.desc4"), StatCollector.translateToLocal("hats.gui.helpInfo11.desc5"), StatCollector.translateToLocal("hats.gui.helpInfo11.desc6")};
   private static final String[] helpInfo12 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo12.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo12.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo12.desc3")};
   private static final String[] helpInfo13 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo13.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo13.desc2")};
   private static final String[] helpInfo14 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo14.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo14.desc2")};
   private static final String[] helpInfo15 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo15.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo15.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo15.desc3")};
   private static final String[] helpInfo16 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo16.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo16.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo16.desc3"), StatCollector.translateToLocal("hats.gui.helpInfo16.desc4")};
   private static final String[] helpInfo17 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo17.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo17.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo17.desc3")};
   private static final String[] helpInfo18 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo18.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo18.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo18.desc3")};
   private static final String[] helpInfo19 = new String[]{StatCollector.translateToLocal("hats.gui.helpInfo19.desc1"), StatCollector.translateToLocal("hats.gui.helpInfo19.desc2"), StatCollector.translateToLocal("hats.gui.helpInfo19.desc3")};


   public GuiHatSelection(EntityPlayer ply) {
      CommonProxy var10000;
      if(Hats.config.getSessionInt("playerHatsMode") >= 4) {
         if(Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
            HatHandler.repopulateHatsList();
         } else {
            var10000 = Hats.proxy;
            TickHandlerClient var7 = CommonProxy.tickHandlerClient;
            HashMap var10001 = new HashMap(CommonProxy.tickHandlerClient.serverHats);
            CommonProxy var10003 = Hats.proxy;
            var7.availableHats = var10001;
         }
      }

      this.player = ply;
      CommonProxy var5 = Hats.proxy;
      this.hat = (EntityHat)CommonProxy.tickHandlerClient.hats.get(this.player.getCommandSenderName());
      ArrayList list = new ArrayList();
      var10000 = Hats.proxy;
      Iterator i$ = CommonProxy.tickHandlerClient.availableHats.entrySet().iterator();

      Entry e;
      while(i$.hasNext()) {
         e = (Entry)i$.next();
         list.add(e.getKey());
      }

      Collections.sort(list);

      for(int var6 = list.size() - 1; var6 >= 0; --var6) {
         if(Hats.config.getInt("showContributorHatsInGui") == 0 && ((String)list.get(var6)).startsWith("(C) ")) {
            list.remove(var6);
         }
      }

      this.availableHats = ImmutableList.copyOf(list);
      this.hatsToShow = new ArrayList(this.availableHats);
      Collections.sort(this.hatsToShow);
      this.categories = new ArrayList();
      i$ = HatHandler.categories.entrySet().iterator();

      while(i$.hasNext()) {
         e = (Entry)i$.next();
         if(!((String)e.getKey()).equalsIgnoreCase("Favourites")) {
            this.categories.add(e.getKey());
         }
      }

      Collections.sort(this.categories);
      if(this.hat != null) {
         this.prevHatName = this.hat.hatName;
         this.prevColourR = this.colourR = this.hat.getR();
         this.prevColourG = this.colourG = this.hat.getG();
         this.prevColourB = this.colourB = this.hat.getB();
         this.prevAlpha = this.alpha = this.hat.getA();
      }

      this.pageNumber = 0;
      this.view = 0;
      this.rand = new Random();
   }

   public void initGui() {
      super.initGui();
      if(this.hat != null && this.player != null) {
         Keyboard.enableRepeatEvents(true);
         String[] enabledBtn = Hats.config.getString("personalizeEnabled").split(" ");
         this.enabledButtons.clear();

         int i;
         for(i = 0; i < enabledBtn.length; ++i) {
            if(enabledBtn[i].equalsIgnoreCase("9")) {
               this.enabledSearchBar = true;
            } else if(!this.enabledButtons.contains(enabledBtn[i])) {
               this.enabledButtons.add(enabledBtn[i]);
            }
         }

         super.buttonList.clear();
         this.guiLeft = (super.width - this.xSize) / 2;
         this.guiTop = (super.height - this.ySize) / 2;
         super.buttonList.add(new GuiButton(1, super.width / 2 - 6, super.height / 2 + 54, 20, 20, "<"));
         super.buttonList.add(new GuiButton(3, super.width / 2 + 62, super.height / 2 + 54, 20, 20, ">"));
         super.buttonList.add(new GuiButton(2, super.width / 2 + 16, super.height / 2 + 54, 44, 20, I18n.format("gui.done", new Object[0])));
         this.addToolButton(8);
         this.addToolButton(9);
         this.addToolButton(10);
         this.addToolButton(11);
         this.addToolButton(12);
         this.addToolButton(13);
         this.addToolButton(14);
         this.addToolButton(15);
         super.buttonList.add(new GuiButton(4, super.width - 22, 2, 20, 20, "X"));
         this.pageNumber = 0;
         if(!this.hat.hatName.equalsIgnoreCase("")) {
            for(i = 0; i < this.hatsToShow.size(); ++i) {
               String hatName = (String)this.hatsToShow.get(i);
               if(hatName.equalsIgnoreCase(this.hat.hatName)) {
                  i -= i % 6;
                  this.pageNumber = i / 6;
                  break;
               }
            }
         }

         this.updateButtonList();
         this.searchBar = new GuiTextField(super.fontRendererObj, super.width / 2 - 65, super.height - 24, 150, 20);
         this.searchBar.setMaxStringLength(255);
         this.searchBar.setText(StatCollector.translateToLocal("hats.gui.search"));
         this.searchBar.setTextColor(11184810);
         this.searchBar.setVisible(this.enabledSearchBar);
      } else {
         super.mc.displayGuiScreen((GuiScreen)null);
      }

   }

   public void addToolButton(int id) {
      boolean enabled = false;

      for(int btn = 0; btn < this.enabledButtons.size(); ++btn) {
         if(((String)this.enabledButtons.get(btn)).equalsIgnoreCase(Integer.toString(id - 7))) {
            super.buttonList.add(new GuiButton(id, super.width / 2 + 89, super.height / 2 - 85 + btn * 21, 20, 20, ""));
            enabled = true;
            break;
         }
      }

      if(!enabled) {
         GuiButton var4 = new GuiButton(id, super.width - 24, super.height / 2 - 93 + (id - 8) * 21, 20, 20, "");
         var4.visible = false;
         super.buttonList.add(var4);
      }

   }

   public void updateScreen() {
      this.searchBar.updateCursorCounter();
      if(this.searchBar.isFocused()) {
         this.searchBar.setVisible(true);
      } else {
         this.searchBar.setVisible(this.enabledSearchBar);
      }

      if(this.favourite > 0) {
         --this.favourite;
      }

   }

   public void onGuiClosed() {
      if(!this.confirmed && this.hat != null) {
         this.hat.hatName = this.prevHatName;
         this.hat.setR(this.prevColourR);
         this.hat.setG(this.prevColourG);
         this.hat.setB(this.prevColourB);
         this.hat.setA(this.prevAlpha);
      }

      Keyboard.enableRepeatEvents(false);
   }

   protected void keyTyped(char c, int i) {
      if(this.settingKey) {
         KeyBind gameSettings = (KeyBind)Hats.config.keyBindMap.get("guiKeyBind");
         KeyBind s = new KeyBind(i, false, false, false, false);
         Hats.config.keyBindMap.put("guiKeyBind", iChunUtil.proxy.registerKeyBind(s, gameSettings));
         Hats.config.get("guiKeyBind").set(i);

         for(int i$ = 0; i$ < super.buttonList.size(); ++i$) {
            GuiButton hat1 = (GuiButton)super.buttonList.get(i$);
            if(hat1.id == 22) {
               hat1.displayString = "GUI: " + Keyboard.getKeyName(i);
               break;
            }
         }

         this.settingKey = false;
         Hats.config.save();
      } else if(!this.personalizing) {
         this.searchBar.textboxKeyTyped(c, i);
         if(this.searchBar.isFocused()) {
            this.onSearch();
         }

         if(i == 1) {
            if(this.searchBar.isFocused()) {
               this.searchBar.setText("");
               this.searchBar.setFocused(false);
               this.onSearchBarInteract();
            } else {
               this.exitWithoutUpdate();
               super.mc.setIngameFocus();
            }
         }

         if(!this.searchBar.isFocused()) {
            GameSettings var7 = Minecraft.getMinecraft().gameSettings;
            if(i == 19) {
               super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
               this.randomize();
            } else if(i != 15 && i != var7.keyBindChat.getKeyCode()) {
               if(i == 35 && (!this.hat.hatName.equalsIgnoreCase("") || this.view != 0)) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.toggleHatsColourizer();
               } else if(i == 49 && !this.hat.hatName.equalsIgnoreCase("")) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.removeHat();
               } else if(i == 46) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.showCategories();
               } else if(i == 33) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  if((this.view == 0 || this.view == 3) && isShiftKeyDown()) {
                     String var8 = "";
                     Iterator var9 = this.availableHats.iterator();

                     while(var9.hasNext()) {
                        String var10 = (String)var9.next();
                        if(var10.toLowerCase().equalsIgnoreCase(this.hat.hatName)) {
                           var8 = var10;
                        }
                     }

                     if(!var8.equalsIgnoreCase("")) {
                        if(HatHandler.isInFavourites(var8)) {
                           HatHandler.removeFromCategory(var8, "Favourites");
                        } else {
                           HatHandler.addToCategory(var8, "Favourites");
                        }

                        if(this.view == 3 && this.category.equalsIgnoreCase("Favourites")) {
                           this.showCategory("Favourites");
                        }

                        this.favourite = 6;
                     }
                  } else {
                     this.showCategory("Favourites");
                  }
               } else if(i == 25) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.personalize();
               }
            } else {
               this.searchBar.setFocused(true);
               this.onSearchBarInteract();
            }

            if(this.view == 0) {
               if((i == var7.keyBindLeft.getKeyCode() || i == 203) && this.pageNumber > 0) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.switchPage(true);
               } else if((i == var7.keyBindRight.getKeyCode() || i == 205) && (this.pageNumber + 1) * 6 < this.hatsToShow.size()) {
                  super.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                  this.switchPage(false);
               }
            }
         } else if(i == 28 && (this.adding || this.renaming) && this.view == 2 && !this.invalidFolderName && (this.adding && this.addCategory(this.searchBar.getText().trim()) || this.renaming && this.renameCategory(this.selectedButtonName, this.searchBar.getText().trim()))) {
            this.searchBar.setText("");
            this.onSearchBarInteract();
            this.updateButtonList();
         }
      }

   }

   protected void mouseClicked(int par1, int par2, int par3) {
      if(this.settingKey) {
         KeyBind flag = (KeyBind)Hats.config.keyBindMap.get("guiKeyBind");
         KeyBind bind = new KeyBind(par3 - 100, false, false, false, false);
         Hats.config.keyBindMap.put("guiKeyBind", iChunUtil.proxy.registerKeyBind(bind, flag));
         Hats.config.get("guiKeyBind").set(par3 - 100);

         for(int i = 0; i < super.buttonList.size(); ++i) {
            GuiButton btn = (GuiButton)super.buttonList.get(i);
            if(btn.id == 22) {
               btn.displayString = "GUI: " + GameSettings.getKeyDisplayString(par3 - 100);
               break;
            }
         }

         this.settingKey = false;
         Hats.config.save();
      } else {
         super.mouseClicked(par1, par2, par3);
         boolean var8 = par1 >= super.width / 2 - 65 && par1 < super.width / 2 - 65 + super.width && par2 >= super.height - 24 && par2 < super.height - 24 + super.height;
         if(this.enabledSearchBar) {
            if(!this.personalizing) {
               this.searchBar.mouseClicked(par1, par2, par3);
               if(par3 == 1 && var8) {
                  this.searchBar.setText("");
                  this.onSearch();
               }

               this.onSearchBarInteract();
            } else if(var8) {
               this.toggleSearchBar();
            }
         }
      }

   }

   protected void mouseMovedOrUp(int par1, int par2, int par3) {
      super.mouseMovedOrUp(par1, par2, par3);
      if(this.adding || this.renaming) {
         this.searchBar.setFocused(true);
         this.onSearchBarInteract();
         this.onSearch();
      }

      this.justClickedButton = false;
   }

   public void onSearch() {
      String s;
      if(!this.adding && !this.renaming) {
         if(!this.searchBar.getText().equalsIgnoreCase("") && (this.hasClicked || !this.searchBar.getText().equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.search")))) {
            String var12 = this.searchBar.getText();
            ArrayList var15 = new ArrayList();
            Iterator var16 = (this.view == 0?this.availableHats:(this.view == 3?this.categoryHats:this.categories)).iterator();

            while(var16.hasNext()) {
               s = (String)var16.next();
               if(this.view != 2 || !s.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.allHats")) && !s.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.addNew"))) {
                  if(s.toLowerCase().startsWith(var12.toLowerCase())) {
                     if(!var15.contains(s)) {
                        var15.add(s);
                     }
                  } else {
                     String[] split = s.split(" ");
                     String[] arr$ = split;
                     int len$ = split.length;

                     for(int i$1 = 0; i$1 < len$; ++i$1) {
                        String s1 = arr$[i$1];
                        if(s1.toLowerCase().startsWith(var12.toLowerCase())) {
                           if(!var15.contains(s)) {
                              var15.add(s);
                           }
                           break;
                        }
                     }
                  }
               }
            }

            if(var15.size() == 0) {
               this.searchBar.setTextColor(16733525);
               this.hatsToShow = new ArrayList(this.view == 0?this.availableHats:(this.view == 3?this.categoryHats:this.categories));
               Collections.sort(this.hatsToShow);
               if(this.view == 2) {
                  this.hatsToShow.add(0, StatCollector.translateToLocal("hats.gui.allHats"));
                  this.hatsToShow.add(StatCollector.translateToLocal("hats.gui.addNew"));
               }
            } else {
               this.searchBar.setTextColor(14737632);
               this.pageNumber = 0;
               this.hatsToShow = new ArrayList(var15);
               Collections.sort(this.hatsToShow);
            }
         } else {
            this.searchBar.setTextColor(14737632);
            this.hatsToShow = new ArrayList(this.view == 0?this.availableHats:(this.view == 3?this.categoryHats:this.categories));
            Collections.sort(this.hatsToShow);
            if(this.view == 2) {
               this.hatsToShow.add(0, StatCollector.translateToLocal("hats.gui.allHats"));
               this.hatsToShow.add(StatCollector.translateToLocal("hats.gui.addNew"));
            }
         }

         this.updateButtonList();
      } else {
         this.invalidFolderName = false;
         this.searchBar.setTextColor(14737632);
         Iterator query = this.categories.iterator();

         while(query.hasNext()) {
            String matches = (String)query.next();
            if(matches.equalsIgnoreCase(this.searchBar.getText())) {
               this.searchBar.setTextColor(16733525);
               this.invalidFolderName = true;
            }
         }

         String[] var10 = invalidChars;
         int var13 = var10.length;

         for(int i$ = 0; i$ < var13; ++i$) {
            s = var10[i$];
            if(this.searchBar.getText().contains(s)) {
               this.searchBar.setTextColor(16733525);
               this.invalidFolderName = true;
            }
         }

         if(this.searchBar.getText().equalsIgnoreCase("Favourites") || this.searchBar.getText().equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.allHats")) || this.searchBar.getText().equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.addNew"))) {
            this.searchBar.setTextColor(16733525);
            this.invalidFolderName = true;
         }

         if(this.searchBar.getText().equalsIgnoreCase("")) {
            this.invalidFolderName = true;
         }

         for(int var11 = 0; var11 < super.buttonList.size(); ++var11) {
            GuiButton var14 = (GuiButton)super.buttonList.get(var11);
            if(var14.id == 17) {
               var14.enabled = !this.invalidFolderName;
               break;
            }
         }
      }

   }

   public void onSearchBarInteract() {
      if(this.searchBar.isFocused()) {
         this.searchBar.setTextColor(14737632);
         if(!this.hasClicked && this.searchBar.getText().equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.search"))) {
            this.hasClicked = true;
            this.searchBar.setText("");
            this.onSearch();
         }
      } else {
         this.searchBar.setTextColor(11184810);
         if(this.searchBar.getText().equalsIgnoreCase("")) {
            this.hasClicked = false;
            if(!this.adding && !this.renaming) {
               this.searchBar.setText(StatCollector.translateToLocal("hats.gui.search"));
               if(this.view == 2) {
                  this.updateButtonList();
               }
            }
         }
      }

   }

   protected void actionPerformed(GuiButton btn) {
      if(btn.id == 27) {
         //FMLClientHandler.instance().displayGuiScreen(Minecraft.getMinecraft().thePlayer, new GuiTradeMaker());
      } else {
         if(btn.id == 28) {
            CommonProxy var10000 = Hats.proxy;
            if(CommonProxy.tickHandlerClient.tradeReq != null) {
               var10000 = Hats.proxy;
               if(CommonProxy.tickHandlerClient.tradeReqTimeout > 0) {
                  EnumMap var13 = Hats.channels;
                  PacketString var10001 = new PacketString(1, CommonProxy.tickHandlerClient.tradeReq);
                  CommonProxy var10004 = Hats.proxy;
                  PacketHandler.sendToServer(var13, var10001);
                  return;
               }
            }
         }

         if(btn.id == 2) {
            if(this.personalizing) {
               this.personalize();
            } else {
               this.exitAndUpdate();
            }
         }

         int i;
         GuiButton button;
         if(this.personalizing) {
            if(btn.id >= 8 && btn.id <= 16 && !this.justClickedButton) {
               this.justClickedButton = true;
               this.toggleVisibility(btn);
               if(btn.id == 16) {
                  super.buttonList.remove(btn);
               }
            } else if(btn.id == 22) {
               this.settingKey = true;
               btn.displayString = "GUI: >???<";
            } else if(btn.id == 23) {
               Hats.config.get("renderInFirstPerson").set(Hats.config.getInt("renderInFirstPerson") == 1?0:1);
               btn.displayString = StatCollector.translateToLocal("hats.gui.firstPerson") + ": " + (Hats.config.getInt("renderInFirstPerson") == 1?StatCollector.translateToLocal("gui.yes"):StatCollector.translateToLocal("gui.no"));
            } else if(btn.id == 26) {
               Hats.config.get("renderHats").set(Hats.config.getInt("renderHats") == 1?0:1);
               Hats.config.updateSession("renderHats", Integer.valueOf(Hats.config.getInt("renderHats")));
               btn.displayString = StatCollector.translateToLocal("hats.gui.showHats") + ": " + (Hats.config.getInt("renderHats") == 1?StatCollector.translateToLocal("gui.yes"):StatCollector.translateToLocal("gui.no"));
            } else if(btn.id == 24) {
               this.enabledSearchBar = false;
               this.toggleSearchBar();
               this.enabledButtons.clear();
               this.enabledButtons.add("1");
               this.enabledButtons.add("2");
               this.enabledButtons.add("3");
               this.enabledButtons.add("4");
               this.enabledButtons.add("5");
               this.enabledButtons.add("6");
               this.enabledButtons.add("7");
               this.enabledButtons.add("8");

               for(i = super.buttonList.size() - 1; i >= 0; --i) {
                  button = (GuiButton)super.buttonList.get(i);
                  if(button.id >= 8 && button.id <= 15) {
                     button.xPosition = super.width / 2 + 89;
                     button.yPosition = super.height / 2 - 85 + (button.id - 8) * 21;
                  } else if(button.id == 16) {
                     super.buttonList.remove(i);
                  }
               }
            }
         } else if(btn.id == 1) {
            this.switchPage(true);
         } else if(btn.id == 3) {
            this.switchPage(false);
         } else if(btn.id == 4) {
            this.exitWithoutUpdate();
         } else if(btn.id == 8) {
            this.removeHat();
         } else if(btn.id == 9) {
            this.toggleHatsColourizer();
         } else if(btn.id == 10) {
            this.randomize();
         } else if(btn.id == 14) {
            this.reloadHatsAndReopenGUI();
         } else if(btn.id == 11) {
            this.showCategory("Favourites");
         } else if(btn.id == 12) {
            this.showCategories();
         } else if(btn.id == 13) {
            this.personalize();
         } else if(btn.id == 15) {
            ++helpPage;
            if(helpPage >= help.size()) {
               helpPage = 0;
            }
         } else if(btn.id == 18) {
            if((this.adding || this.renaming) && this.view == 2 && this.searchBar.isFocused()) {
               this.searchBar.setText("");
               this.onSearchBarInteract();
               this.updateButtonList();
            } else if(!this.justClickedButton && !this.selectedButtonName.equalsIgnoreCase("")) {
               this.selectedButtonName = "";
               this.updateButtonList();
            }
         } else if(btn.id == 17) {
            if(!this.justClickedButton) {
               if(this.view == 3 && !this.category.equalsIgnoreCase("Favourites") && !this.category.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.contributors"))) {
                  HatHandler.removeFromCategory(this.selectedButtonName, this.category);
                  this.showCategory(this.category);
               } else if(this.view != 0 && this.view != 3) {
                  if((this.adding || this.renaming) && this.view == 2 && this.searchBar.isFocused() && (this.adding && this.addCategory(this.searchBar.getText().trim()) || this.renaming && this.renameCategory(this.selectedButtonName, this.searchBar.getText().trim()))) {
                     this.searchBar.setText("");
                     this.onSearchBarInteract();
                     this.updateButtonList();
                  }
               } else {
                  this.addingToCategory = true;
                  this.showCategories();
               }
            }
         } else if(btn.id == 20) {
            if(!this.justClickedButton && !this.selectedButtonName.equalsIgnoreCase("")) {
               if(!this.deleting) {
                  this.deleting = true;
               } else {
                  this.deleting = false;

                  try {
                     if(this.view == 2) {
                        File var10 = new File(HatHandler.hatsFolder, "/" + this.selectedButtonName);
                        if(var10.exists() && var10.isDirectory()) {
                           File[] var11 = var10.listFiles();
                           File[] arr$ = var11;
                           int len$ = var11.length;

                           for(int i$ = 0; i$ < len$; ++i$) {
                              File file = arr$[i$];
                              File hat = new File(HatHandler.hatsFolder, file.getName());
                              if(!hat.isDirectory() && hat.getName().endsWith(".tc2")) {
                                 if(!hat.exists()) {
                                    file.renameTo(hat);
                                 }

                                 file.delete();
                              }
                           }

                           if(!var10.delete()) {
                              Hats.console("Cannot delete category \"" + this.selectedButtonName + "\", directory is not empty!", true);
                           }
                        }
                     }

                     if(this.view == 0 || this.view == 3) {
                        HatHandler.deleteHat(this.selectedButtonName, isShiftKeyDown());
                     }
                  } catch (Exception var9) {
                     Hats.console("Failed to delete " + (this.view == 2?"category":"hat") + ": " + this.selectedButtonName, true);
                  }

                  this.selectedButtonName = "";
                  this.updateButtonList();
                  this.reloadHatsAndReopenGUI();
               }
            }
         } else if(btn.id == 19) {
            if(!this.justClickedButton && !this.selectedButtonName.equalsIgnoreCase("")) {
               this.renaming = true;

               for(i = super.buttonList.size() - 1; i >= 0; --i) {
                  button = (GuiButton)super.buttonList.get(i);
                  if(button.id == 19 || button.id == 20 || button.id == 18) {
                     super.buttonList.remove(i);
                  }
               }

               super.buttonList.add(new GuiButton(17, btn.xPosition + 16 - 7, btn.yPosition, 20, 20, ""));
               super.buttonList.add(new GuiButton(18, btn.xPosition + 52 - 7, btn.yPosition, 20, 20, ""));
               this.searchBar.setText(this.selectedButtonName);
            }
         } else if(btn.id == 21) {
            if(!this.justClickedButton) {
               if(!this.selectedButtonName.equalsIgnoreCase("") && HatHandler.isInFavourites(this.selectedButtonName)) {
                  HatHandler.removeFromCategory(this.selectedButtonName, "Favourites");
                  if(this.view == 3 && this.category.equalsIgnoreCase("Favourites")) {
                     this.showCategory("Favourites");
                  }
               } else {
                  HatHandler.addToCategory(this.selectedButtonName, "Favourites");
               }
            }
         } else if(btn.id >= 600) {
            this.justClickedButton = true;
            if(isShiftKeyDown()) {
               this.updateButtonList();
               this.selectedButtonName = btn.displayString;

               for(i = super.buttonList.size() - 1; i >= 0; --i) {
                  button = (GuiButton)super.buttonList.get(i);
                  if(button.id == btn.id) {
                     super.buttonList.remove(button);
                     break;
                  }
               }

               super.buttonList.add(new GuiButton(17, btn.xPosition + 1, btn.yPosition, 20, 20, ""));
               super.buttonList.add(new GuiButton(21, btn.xPosition + 23, btn.yPosition, 20, 20, ""));
               GuiButton var12 = new GuiButton(20, btn.xPosition + 45, btn.yPosition, 20, 20, "");
               if(HatHandler.isContributor(this.selectedButtonName)) {
                  var12.enabled = false;
               }

               super.buttonList.add(var12);
               super.buttonList.add(new GuiButton(18, btn.xPosition + 67, btn.yPosition, 20, 20, ""));
            } else {
               this.hat.hatName = btn.displayString.toLowerCase();
               this.colourR = this.colourG = this.colourB = this.alpha = 255;
               this.hat.setR(255);
               this.hat.setG(255);
               this.hat.setB(255);
               this.hat.setA(255);
               this.updateButtonList();
            }
         } else if(btn.id >= 30) {
            this.justClickedButton = true;
            if(this.addingToCategory) {
               HatHandler.addToCategory(this.selectedButtonName, btn.displayString);
               this.view = 1;
               this.category = "";
               this.toggleHatsColourizer();
            } else if(btn.displayString.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.addNew"))) {
               this.updateButtonList();

               for(i = super.buttonList.size() - 1; i >= 0; --i) {
                  button = (GuiButton)super.buttonList.get(i);
                  if(button.id == btn.id) {
                     super.buttonList.remove(button);
                     break;
                  }
               }

               super.buttonList.add(new GuiButton(17, btn.xPosition + 16, btn.yPosition, 20, 20, ""));
               super.buttonList.add(new GuiButton(18, btn.xPosition + 52, btn.yPosition, 20, 20, ""));
               this.adding = true;
               this.searchBar.setText("");
            } else if(isShiftKeyDown() && !btn.displayString.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.allHats")) && !btn.displayString.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.contributors"))) {
               this.updateButtonList();
               this.selectedButtonName = btn.displayString;

               for(i = super.buttonList.size() - 1; i >= 0; --i) {
                  button = (GuiButton)super.buttonList.get(i);
                  if(button.id == btn.id) {
                     super.buttonList.remove(button);
                     break;
                  }
               }

               super.buttonList.add(new GuiButton(19, btn.xPosition + 7, btn.yPosition, 20, 20, ""));
               super.buttonList.add(new GuiButton(20, btn.xPosition + 34, btn.yPosition, 20, 20, ""));
               super.buttonList.add(new GuiButton(18, btn.xPosition + 61, btn.yPosition, 20, 20, ""));
            } else {
               this.showCategory(btn.displayString);
            }
         }

      }
   }

   public void exitAndUpdate() {
      this.confirmed = true;
      super.mc.displayGuiScreen((GuiScreen)null);
      if(Hats.config.getSessionInt("serverHasMod") != 1) {
         Hats.config.get("favouriteHat").set(this.hat.hatName);
         String r = Integer.toHexString(this.colourR);
         String b = Integer.toHexString(this.colourG);
         String g = Integer.toHexString(this.colourB);
         if(r.length() < 2) {
            r = "0" + r;
         }

         if(g.length() < 2) {
            g = "0" + g;
         }

         if(b.length() < 2) {
            b = "0" + b;
         }

         String name = "#" + r + g + b;
         Hats.config.get("favouriteHatColourizer").set(name);
         Hats.favouriteHatInfo = new HatInfo(this.hat.hatName, this.colourR, this.colourG, this.colourB, this.alpha);
         Hats.config.save();
      } else if(this.player != null && !this.player.isDead && this.player.isEntityAlive()) {
         CommonProxy var10000 = Hats.proxy;
         CommonProxy.tickHandlerClient.playerWornHats.put(this.player.getCommandSenderName(), new HatInfo(this.hat.hatName, this.colourR, this.colourG, this.colourB, this.alpha));
         PacketHandler.sendToServer(Hats.channels, new PacketPlayerHatSelection(this.hat.hatName, this.colourR, this.colourG, this.colourB, this.alpha));
      }

   }

   public void exitWithoutUpdate() {
      super.mc.displayGuiScreen((GuiScreen)null);
      this.hat.hatName = this.prevHatName;
      this.hat.setR(this.prevColourR);
      this.hat.setG(this.prevColourG);
      this.hat.setB(this.prevColourB);
      this.hat.setA(this.prevAlpha);
   }

   public void updateButtonList() {
      this.adding = false;
      this.deleting = false;
      this.renaming = false;
      if(this.view != 2) {
         this.addingToCategory = false;
      }

      int btn;
      for(btn = super.buttonList.size() - 1; btn >= 0; --btn) {
         GuiButton i = (GuiButton)super.buttonList.get(btn);
         if((i.id < 5 || i.id > 7) && i.id != 29 && i.id < 30 && i.id != 17 && i.id != 18 && i.id != 19 && i.id != 20 && i.id != 21 && i.id != 27 && i.id != 28) {
            if(i.id == 1) {
               if(this.pageNumber != 0 && this.view != 1 && !this.personalizing) {
                  i.enabled = true;
               } else {
                  i.enabled = false;
               }
            } else if(i.id == 3) {
               if((this.pageNumber + 1) * 6 < this.hatsToShow.size() && this.view != 1 && !this.personalizing) {
                  i.enabled = true;
               } else {
                  i.enabled = false;
               }
            } else if(i.id == 8) {
               if(this.hat.hatName.equalsIgnoreCase("")) {
                  i.enabled = false;
               } else {
                  i.enabled = true;
               }
            } else if(i.id == 9) {
               if(this.hat.hatName.equalsIgnoreCase("") && this.view == 0) {
                  i.enabled = false;
               } else {
                  i.enabled = true;
               }
            } else if(i.id == 12) {
               if(this.view == 2) {
                  i.enabled = false;
               } else {
                  i.enabled = true;
               }
            } else if(i.id == 11) {
               if(this.view == 3 && this.category.equalsIgnoreCase("Favourites")) {
                  i.enabled = false;
               } else {
                  i.enabled = true;
               }
            }
         } else {
            super.buttonList.remove(btn);
         }
      }

      if(!this.personalizing) {
         int var5;
         if(this.view != 0 && this.view != 2 && this.view != 3) {
            if(this.view == 1) {
               btn = 0;

               for(var5 = 0; var5 < 4; ++var5) {
                  GuiSlider var8 = new GuiSlider(var5 == 3?29:5 + var5, super.width / 2 - 6, super.height / 2 - 78 + 22 * btn, 88, var5 == 0?StatCollector.translateToLocal("item.fireworksCharge.red") + ": ":(var5 == 1?StatCollector.translateToLocal("item.fireworksCharge.green") + ": ":(var5 == 2?StatCollector.translateToLocal("item.fireworksCharge.blue") + ": ":StatCollector.translateToLocal("hats.gui.alpha") + ": ")), "", 0.0D, 255.0D, var5 == 0?(double)this.colourR:(var5 == 1?(double)this.colourG:(var5 == 2?(double)this.colourB:(double)this.alpha)), false, true, this);
                  super.buttonList.add(var8);
                  ++btn;
               }

               this.currentDisplay = StatCollector.translateToLocal("hats.gui.colorizer");
            }
         } else {
            btn = 0;

            for(var5 = this.pageNumber * 6; var5 < this.hatsToShow.size() && var5 < (this.pageNumber + 1) * 6; ++var5) {
               String hatName = (String)this.hatsToShow.get(var5);
               GuiButton btn1 = new GuiButton(this.view != 0 && this.view != 3?30 + var5:600 + var5, super.width / 2 - 6, super.height / 2 - 78 + 22 * btn, 88, 20, hatName);
               if((this.view == 0 || this.view == 3) && hatName.toLowerCase().equalsIgnoreCase(this.hat.hatName)) {
                  btn1.enabled = false;
               }

               super.buttonList.add(btn1);
               ++btn;
               if(btn == 6) {
                  boolean var6 = false;
                  break;
               }
            }

            var5 = (int)Math.ceil((double)((float)this.hatsToShow.size() / 6.0F));
            if(var5 <= 0) {
               var5 = 1;
            }

            this.currentDisplay = (this.view == 0?StatCollector.translateToLocal("hats.gui.allHats"):(this.view == 3?StatCollector.translateToLocal("hats.gui.category") + " - " + this.category:StatCollector.translateToLocal("hats.gui.categories"))) + " (" + (this.pageNumber + 1) + "/" + var5 + ")";
         }

         GuiButton var7 = new GuiButton(27, this.guiLeft - 21, this.guiTop + this.ySize - 23, 20, 20, "");
         var7.enabled = Hats.config.getSessionInt("serverHasMod") == 1;
         super.buttonList.add(var7);
         var7 = new GuiButton(28, this.guiLeft - 21, this.guiTop + this.ySize - 45, 20, 20, "");
         CommonProxy var10001 = Hats.proxy;
         var7.enabled = CommonProxy.tickHandlerClient.tradeReqTimeout > 0;
         super.buttonList.add(var7);
      } else {
         super.buttonList.add(new GuiButton(22, super.width / 2 - 6, super.height / 2 - 78, 88, 20, "GUI: " + GameSettings.getKeyDisplayString(Hats.config.getKeyBind("guiKeyBind").keyIndex)));
         super.buttonList.add(new GuiButton(23, super.width / 2 - 6, super.height / 2 - 78 + 22, 88, 20, StatCollector.translateToLocal("hats.gui.firstPerson") + ": " + (Hats.config.getInt("renderInFirstPerson") == 1?StatCollector.translateToLocal("gui.yes"):StatCollector.translateToLocal("gui.no"))));
         super.buttonList.add(new GuiButton(26, super.width / 2 - 6, super.height / 2 - 78 + 44, 88, 20, StatCollector.translateToLocal("hats.gui.showHats") + ": " + (Hats.config.getInt("renderHats") == 1?StatCollector.translateToLocal("gui.yes"):StatCollector.translateToLocal("gui.no"))));
         super.buttonList.add(new GuiButton(24, super.width / 2 - 6, super.height / 2 - 78 + 110, 88, 20, StatCollector.translateToLocal("hats.gui.resetSide")));
         if(Hats.config.getSessionInt("playerHatsMode") < 4) {
            super.buttonList.add(new GuiSlider(25, super.width / 2 - 6, super.height / 2 - 78 + 66, 88, StatCollector.translateToLocal("hats.gui.randomobs") + ": ", "%", 0.0D, 100.0D, (double)Hats.config.getInt("randomMobHat"), false, true, this));
         }

         this.currentDisplay = StatCollector.translateToLocal("hats.gui.personalize");
      }

   }

   public void removeHat() {
      this.hat.hatName = "";
      this.updateButtonList();
   }

   public void reloadHatsAndReopenGUI() {
      for(int k1 = super.buttonList.size() - 1; k1 >= 0; --k1) {
         GuiButton btn1 = (GuiButton)super.buttonList.get(k1);
         if(!(btn1 instanceof GuiSlider) && btn1.id != 4) {
            btn1.enabled = false;
         }
      }

      Hats.proxy.getHatMobModSupport();
      Hats.proxy.getHatsAndOpenGui();
   }

   public void switchPage(boolean left) {
      if(left) {
         --this.pageNumber;
         if(this.pageNumber < 0) {
            this.pageNumber = 0;
         }

         this.updateButtonList();
      } else {
         ++this.pageNumber;
         if(this.pageNumber * 6 >= this.hatsToShow.size()) {
            --this.pageNumber;
         }

         this.updateButtonList();
      }

   }

   public void toggleHatsColourizer() {
      if(this.view == 1 && !isShiftKeyDown() && !this.category.equalsIgnoreCase("")) {
         this.view = 3;
      } else if(this.view == 3 && !isShiftKeyDown()) {
         this.view = 1;
      } else {
         this.view = this.view > 0?0:1;
      }

      this.hatsToShow = new ArrayList(this.view == 0?this.availableHats:this.categoryHats);
      Collections.sort(this.hatsToShow);
      this.searchBar.setText("");
      this.onSearchBarInteract();
      this.updateButtonList();
   }

   public boolean addCategory(String s) {
      if(this.invalidFolderName) {
         return false;
      } else {
         try {
            File e = new File(HatHandler.hatsFolder, "/" + s);
            if(!e.mkdirs()) {
               return false;
            } else {
               this.categories.add(s);
               Collections.sort(this.categories);
               HatHandler.categories.put(s, new ArrayList());
               this.hatsToShow = new ArrayList(this.categories);
               Collections.sort(this.hatsToShow);
               this.hatsToShow.add(0, StatCollector.translateToLocal("hats.gui.allHats"));
               this.hatsToShow.add(StatCollector.translateToLocal("hats.gui.addNew"));
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public boolean renameCategory(String oriName, String newName) {
      if(this.invalidFolderName) {
         return false;
      } else {
         try {
            File e = new File(HatHandler.hatsFolder, "/" + oriName);
            File newCat = new File(HatHandler.hatsFolder, "/" + newName);
            if(!e.exists()) {
               return false;
            } else if(!e.renameTo(newCat)) {
               return false;
            } else {
               this.reloadHatsAndReopenGUI();
               return true;
            }
         } catch (Exception var5) {
            return false;
         }
      }
   }

   public void showCategory(String s) {
      if(s.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.allHats"))) {
         this.view = 1;
         this.category = "";
         this.toggleHatsColourizer();
      } else {
         this.pageNumber = 0;
         this.view = 3;
         this.category = s;
         ArrayList hatsList = (ArrayList)HatHandler.categories.get(s);
         if(hatsList == null) {
            hatsList = new ArrayList();
         }

         ArrayList hatsCopy = new ArrayList(hatsList);
         if(Hats.config.getSessionInt("playerHatsMode") >= 4 && !super.mc.thePlayer.capabilities.isCreativeMode) {
            for(int i = hatsCopy.size() - 1; i >= 0; --i) {
               String hatName = (String)hatsCopy.get(i);
               CommonProxy var10000 = Hats.proxy;
               if(!CommonProxy.tickHandlerClient.serverHats.containsKey(hatName)) {
                  hatsCopy.remove(i);
               }
            }
         }

         this.categoryHats = new ArrayList(hatsCopy);
         this.hatsToShow = new ArrayList(hatsCopy);
         Collections.sort(this.hatsToShow);
         this.updateButtonList();
      }

   }

   public void randomize() {
      int randVal;
      String categoryName;
      if(this.view != 0 && this.view != 3) {
         if(this.view == 1) {
            if(isShiftKeyDown()) {
               this.colourR = this.colourG = this.colourB = this.alpha = 255;
               this.hat.setR(255);
               this.hat.setG(255);
               this.hat.setB(255);
               this.hat.setA(255);
               this.updateButtonList();
            } else {
               this.randomizeColour();
            }
         } else if(this.view == 2 && this.hatsToShow.size() > 0) {
            randVal = this.rand.nextInt(this.hatsToShow.size());
            categoryName = (String)this.hatsToShow.get(randVal);
            this.showCategory(categoryName);
         }
      } else if(this.hatsToShow.size() > 0) {
         randVal = this.rand.nextInt(this.hatsToShow.size());
         categoryName = (String)this.hatsToShow.get(randVal);
         this.hat.hatName = categoryName.toLowerCase();
         this.colourR = this.colourG = this.colourB = this.alpha = 255;
         this.hat.setR(255);
         this.hat.setG(255);
         this.hat.setB(255);
         this.hat.setA(255);
         this.pageNumber = randVal / 6;
         if(isShiftKeyDown()) {
            this.view = 1;
            this.updateButtonList();
            this.randomizeColour();
            this.view = 0;
         }

         this.updateButtonList();
      }

   }

   public void randomizeColour() {
		for (int k1 = buttonList.size() - 1; k1 >= 0; k1--)
		{
			GuiButton btn1 = (GuiButton)this.buttonList.get(k1);
			if(btn1 instanceof GuiSlider)
			{
				GuiSlider slider = (GuiSlider)btn1;
				if(slider.id >= 5 && slider.id <= 7)
				{
					if(hat.hatName.equalsIgnoreCase(""))
					{
						slider.sliderValue = 0.0F;
					}
					else
					{
						slider.sliderValue = rand.nextFloat();
					}
					slider.updateSlider();
				}
			}
		}
   }

   public void showCategories() {
      if(this.view != 2) {
         this.view = 2;
         this.pageNumber = 0;
         this.searchBar.setText("");
         this.onSearchBarInteract();
         this.hatsToShow = new ArrayList(this.categories);
         Collections.sort(this.hatsToShow);
         if(!this.addingToCategory) {
            this.hatsToShow.add(0, StatCollector.translateToLocal("hats.gui.allHats"));
            this.hatsToShow.add(StatCollector.translateToLocal("hats.gui.addNew"));
         } else {
            this.hatsToShow.remove(StatCollector.translateToLocal("hats.gui.contributors"));
         }

         this.updateButtonList();
      }

   }

   public void personalize() {
      this.showCategory(StatCollector.translateToLocal("hats.gui.allHats"));
      int sb;
      GuiButton i;
      if(!this.personalizing) {
         this.personalizing = true;
         this.randoMob = Hats.config.getInt("randomMobHat");

         for(sb = super.buttonList.size() - 1; sb >= 0; --sb) {
            i = (GuiButton)super.buttonList.get(sb);
            if(i.id >= 8 && i.id <= 15) {
               i.visible = true;
            }

            if(i.id == 16) {
               super.buttonList.remove(sb);
            }
         }

         if(!this.enabledSearchBar) {
            GuiButton var3 = new GuiButton(16, super.width - 24, super.height / 2 - 93 + 168, 20, 20, "");
            super.buttonList.add(var3);
         }

         this.updateButtonList();
      } else {
         this.personalizing = false;

         for(sb = super.buttonList.size() - 1; sb >= 0; --sb) {
            i = (GuiButton)super.buttonList.get(sb);
            if(i.id >= 8 && i.id <= 15 && !this.enabledButtons.contains(Integer.toString(i.id - 7))) {
               i.visible = false;
            }

            if(i.id >= 22 && i.id <= 25 || i.id == 16) {
               super.buttonList.remove(sb);
            }
         }

         StringBuilder var5 = new StringBuilder();

         for(int var4 = 0; var4 < this.enabledButtons.size(); ++var4) {
            var5.append((String)this.enabledButtons.get(var4));
            var5.append(" ");
         }

         if(this.enabledSearchBar) {
            var5.append("9");
         }

         Hats.config.get("personalizeEnabled").set(var5.toString().trim());
         Hats.config.get("randomMobHat").set(this.randoMob);
         Hats.config.save();
         this.updateButtonList();
      }

   }

   public void toggleSearchBar() {
      if(this.enabledSearchBar) {
         this.enabledSearchBar = false;
         this.searchBar.setVisible(false);
         GuiButton btn = new GuiButton(16, super.width - 24, super.height / 2 - 93 + 168, 20, 20, "");
         super.buttonList.add(btn);
      } else {
         this.enabledSearchBar = true;
         this.searchBar.setVisible(true);
      }

   }

   public void toggleVisibility(GuiButton btn) {
      if(btn.id == 16) {
         this.toggleSearchBar();
      } else if(this.enabledButtons.contains(Integer.toString(btn.id - 7))) {
         this.enabledButtons.remove(Integer.toString(btn.id - 7));
         btn.xPosition = super.width - 24;
         btn.yPosition = super.height / 2 - 93 + (btn.id - 8) * 21;

         for(int i = 0; i < super.buttonList.size(); ++i) {
            GuiButton btn1 = (GuiButton)super.buttonList.get(i);
            if(this.enabledButtons.contains(Integer.toString(btn1.id - 7))) {
               for(int i1 = 0; i1 < this.enabledButtons.size(); ++i1) {
                  if(Integer.toString(btn1.id - 7).equalsIgnoreCase((String)this.enabledButtons.get(i1))) {
                     btn1.xPosition = super.width / 2 + 89;
                     btn1.yPosition = super.height / 2 - 85 + i1 * 21;
                     break;
                  }
               }
            }
         }
      } else if(!this.enabledButtons.contains(Integer.toString(btn.id - 7))) {
         this.enabledButtons.add(Integer.toString(btn.id - 7));
         btn.xPosition = super.width / 2 + 89;
         btn.yPosition = super.height / 2 - 85 + (this.enabledButtons.size() - 1) * 21;
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      if(super.mc == null) {
         super.mc = Minecraft.getMinecraft();
         super.fontRendererObj = super.mc.fontRenderer;
      }

      this.drawDefaultBackground();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.getTextureManager().bindTexture(texChooser);
      int k = this.guiLeft;
      int l = this.guiTop;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      super.mc.getTextureManager().bindTexture(texIcons);
      int k1;
      if(this.personalizing) {
         for(k1 = 0; k1 < 8; ++k1) {
            this.drawTexturedModalRect(k + 176, l - 1 + k1 * 21, 190, 16, 22, 22);
         }

         this.drawTexturedModalRect(k - 1, super.height - 29, 0, 16, 190, 29);
      }

      for(k1 = 0; k1 < super.buttonList.size(); ++k1) {
         GuiButton btn = (GuiButton)super.buttonList.get(k1);
         String disp = btn.displayString;
         if(btn.id >= 30) {
            int id = btn.id >= 600?btn.id - 600:btn.id - 30;
            if(this.pageNumber * 6 > id || (this.pageNumber + 1) * 6 <= id) {
               continue;
            }

            if(btn.displayString.length() > 16) {
               btn.displayString = btn.displayString.substring(0, 13) + "...";
            }
         }

         btn.drawButton(super.mc, par1, par2);
         if(!(btn instanceof GuiSlider)) {
            btn.displayString = disp;
         }

         if(btn.id == 9 || btn.id == 8 || btn.id == 10 || btn.id == 15 || btn.id == 14 || btn.id == 11 || btn.id == 12 || btn.id == 13 || btn.id == 17 || btn.id == 18 || btn.id == 19 || btn.id == 20 || btn.id == 21 || btn.id == 16 || btn.id == 27 || btn.id == 28) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            super.mc.getTextureManager().bindTexture(texIcons);
            if(btn.visible) {
               if(btn.id == 9) {
                  this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, this.view != 0 && this.view != 3?0:16, 0, 16, 16);
               } else if(btn.id != 8 && btn.id != 18) {
                  if(btn.id == 10) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 80, 0, 16, 16);
                  } else if(btn.id == 15) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 96, 0, 16, 16);
                  } else if(btn.id == 14) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 48, 0, 16, 16);
                  } else if(btn.id == 11) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2 - (this.favourite > 3?6 - this.favourite:this.favourite), 64, 0, 16, 16);
                  } else if(btn.id == 12) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 112, 0, 16, 16);
                  } else if(btn.id == 13) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 176, 0, 16, 16);
                  } else if(btn.id == 17) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, this.view == 3 && !this.category.equalsIgnoreCase("Favourites") && !this.category.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.contributors"))?224:160, 0, 16, 16);
                  } else if(btn.id == 19) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 192, 0, 16, 16);
                  } else if(btn.id == 20) {
                     if((this.view == 3 || this.view == 0) && isShiftKeyDown()) {
                        this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 208, 0, 16, 16);
                     } else {
                        this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 144, 0, 16, 16);
                     }
                  } else if(btn.id == 21) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 64, 0, 16, 16);
                     if(!this.selectedButtonName.equalsIgnoreCase("") && HatHandler.isInFavourites(this.selectedButtonName)) {
                        this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 32, 0, 16, 16);
                     }
                  } else if(btn.id == 16) {
                     this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 128, 0, 16, 16);
                  } else if(btn.id == 27) {
                     //this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 82, 45, 16, 16);
                  } else if(btn.id == 28) {
                     //this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 98, 45, 16, 16);
                  }
               } else {
                  this.drawTexturedModalRect(btn.xPosition + 2, btn.yPosition + 2, 32, 0, 16, 16);
               }
            }

            GL11.glDisable(3042);
         }
      }

      this.drawString(super.fontRendererObj, StatCollector.translateToLocal("hats.gui.viewing") + ": " + this.currentDisplay, this.guiLeft + 1, this.guiTop - 9, 16777215);
      this.mouseX = (float)par1;
      this.mouseY = (float)par2;
      this.drawSearchBar();
      this.drawPlayerOnGui(k + 42, l + 155, 55, (float)(k + 42) - this.mouseX, (float)(l + 155 - 92) - this.mouseY);
      if(this.player != null && !this.player.capabilities.isCreativeMode && this.tempInfo != null && !this.tempInfo.hatName.isEmpty()) {
         GL11.glDisable(2929);
         FontRenderer var10001 = super.fontRendererObj;
         StringBuilder var10002 = (new StringBuilder()).append(HatHandler.getHatRarityColour(this.tempInfo.hatName).toString());
         Object[] var10004 = new Object[1];
         CommonProxy var10007 = Hats.proxy;
         int var10;
         if(CommonProxy.tickHandlerClient.availableHats.get(HatHandler.getNameForHat(this.tempInfo.hatName)) == null) {
            var10 = 1;
         } else {
            var10007 = Hats.proxy;
            var10 = ((Integer)CommonProxy.tickHandlerClient.availableHats.get(HatHandler.getNameForHat(this.tempInfo.hatName))).intValue();
         }

         var10004[0] = Integer.valueOf(var10);
         this.drawString(var10001, var10002.append(StatCollector.translateToLocalFormatted("hats.gui.hatsCollected", var10004)).toString(), this.guiLeft + 10, this.guiTop + this.ySize - 22, 16777215);
         GL11.glEnable(2929);
      }

      this.drawForeground(par1, par2, par3);
   }

   public void drawForeground(int par1, int par2, float par3) {
      for(int k1 = 0; k1 < super.buttonList.size(); ++k1) {
         GuiButton btn = (GuiButton)super.buttonList.get(k1);
         if(btn.func_146115_a() && !this.personalizing) {
            if(btn.id >= 30 && btn.displayString.length() > 16) {
               this.drawTooltip(Arrays.asList(new String[]{"§7" + btn.displayString}), par1, par2);
            } else if(btn.id == 4) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.discardChanges")}), par1, par2);
            } else if(btn.id == 8) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.removeHat") + " (N)"}), par1, par2);
            } else if(btn.id == 9) {
               this.drawTooltip(Arrays.asList(new String[]{(this.view != 0 && this.view != 3?StatCollector.translateToLocal("hats.gui.hatsList"):StatCollector.translateToLocal("hats.gui.colorizer")) + " (H)"}), par1, par2);
            } else if(btn.id == 10) {
               this.drawTooltip(Arrays.asList(new String[]{(this.view != 0 && this.view != 3?(this.view == 2?StatCollector.translateToLocal("hats.gui.randomCategory"):(isShiftKeyDown()?StatCollector.translateToLocal("hats.gui.resetColor"):StatCollector.translateToLocal("hats.gui.randomColor"))):StatCollector.translateToLocal("hats.gui.randomHat")) + " (R)"}), par1, par2);
            } else if(btn.id == 15) {
               this.drawTooltip(getCurrentHelpText(), par1, par2);
            } else if(btn.id == 14) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.reloadAllHats"), StatCollector.translateToLocal("hats.gui.discardAllChanges")}), par1, par2);
            } else if(btn.id == 11) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.favorites") + " (F)"}), par1, par2);
            } else if(btn.id == 12) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.categories") + " (C)"}), par1, par2);
            } else if(btn.id == 13) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.personalize") + " (P)"}), par1, par2);
            } else if(btn.id == 17) {
               this.drawTooltip(Arrays.asList(new String[]{!this.adding && !this.renaming?(this.view != 0 && (this.view != 3 || !this.category.equalsIgnoreCase("Favourites") && !this.category.equalsIgnoreCase(StatCollector.translateToLocal("hats.gui.contributors")))?StatCollector.translateToLocal("hats.gui.removeFromCategory"):StatCollector.translateToLocal("hats.gui.addToCategory")):(this.invalidFolderName?"§c" + StatCollector.translateToLocal("hats.gui.invalidName"):StatCollector.translateToLocal("hats.gui.addCategory"))}), par1, par2);
            } else if(btn.id == 18) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("gui.cancel")}), par1, par2);
            } else if(btn.id == 20) {
               if(this.deleting) {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.areYouSure"), "", StatCollector.translateToLocal("hats.gui.clickConfirm")}), par1, par2);
               } else if(this.view == 2) {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.deleteCategory"), "", StatCollector.translateToLocal("hats.gui.deleteCategory.desc1"), StatCollector.translateToLocal("hats.gui.deleteCategory.desc2"), StatCollector.translateToLocal("hats.gui.deleteCategory.desc3"), "", StatCollector.translateToLocal("hats.gui.doubleClickConfirm")}), par1, par2);
               } else if(isShiftKeyDown()) {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.disableHat"), "", StatCollector.translateToLocal("hats.gui.disableHat.desc1"), StatCollector.translateToLocal("hats.gui.disableHat.desc2"), StatCollector.translateToLocal("hats.gui.disableHat.desc3"), StatCollector.translateToLocal("hats.gui.disableHat.desc4"), "", StatCollector.translateToLocal("hats.gui.doubleClickConfirm")}), par1, par2);
               } else {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.deleteHat"), "", StatCollector.translateToLocal("hats.gui.deleteHat.desc1"), StatCollector.translateToLocal("hats.gui.deleteHat.desc2"), StatCollector.translateToLocal("hats.gui.deleteHat.desc3"), "", StatCollector.translateToLocal("hats.gui.doubleClickConfirm")}), par1, par2);
               }
            } else if(btn.id == 19) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.renameCategory"), "", StatCollector.translateToLocal("hats.gui.renameCategory.desc1")}), par1, par2);
            } else if(btn.id == 21) {
               if(!this.selectedButtonName.equalsIgnoreCase("") && HatHandler.isInFavourites(this.selectedButtonName)) {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.removeFromFavorites")}), par1, par2);
               } else {
                  this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.addToFavorites")}), par1, par2);
               }
            } else if(btn.id == 24) {
               this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.gui.resetButtonsOnSide")}), par1, par2);
            } else if(btn.id == 27) {
               //this.drawTooltip(Arrays.asList(new String[]{StatCollector.translateToLocal("hats.trade")}), par1, par2);
            } else if(btn.id == 28) {
               String var6;
               String[] var10001;
               label133: {
                  var10001 = new String[1];
                  CommonProxy var10004 = Hats.proxy;
                  if(CommonProxy.tickHandlerClient.tradeReq != null) {
                     var10004 = Hats.proxy;
                     if(CommonProxy.tickHandlerClient.tradeReqTimeout > 0) {
                        Object[] var10005 = new Object[2];
                        CommonProxy var10008 = Hats.proxy;
                        var10005[0] = CommonProxy.tickHandlerClient.tradeReq;
                        var10008 = Hats.proxy;
                        var10005[1] = Integer.valueOf((int)Math.ceil((double)CommonProxy.tickHandlerClient.tradeReqTimeout / 20.0D));
                        var6 = StatCollector.translateToLocalFormatted("hats.trade.acceptRequestSpecific", var10005);
                        break label133;
                     }
                  }

                  var6 = StatCollector.translateToLocal("hats.trade.acceptRequestGeneral");
               }

               var10001[0] = var6;
               this.drawTooltip(Arrays.asList(var10001), par1, par2);
            }
         }
      }

   }

   protected void drawTooltip(List par1List, int par2, int par3) {
      if(!par1List.isEmpty()) {
         GL11.glDisable('\u803a');
         GL11.glDisable(2929);
         int k = 0;
         Iterator iterator = par1List.iterator();

         int j1;
         while(iterator.hasNext()) {
            String i1 = (String)iterator.next();
            j1 = super.fontRendererObj.getStringWidth(i1);
            if(j1 > k) {
               k = j1;
            }
         }

         int var14 = par2 + 12;
         j1 = par3 - 12;
         int k1 = 8;
         if(par1List.size() > 1) {
            k1 += 2 + (par1List.size() - 1) * 10;
         }

         if(var14 + k > super.width) {
            var14 -= 28 + k;
         }

         if(j1 + k1 + 6 > super.height) {
            j1 = super.height - k1 - 6;
         }

         super.zLevel = 300.0F;
         int l1 = -267386864;
         this.drawGradientRect(var14 - 3, j1 - 4, var14 + k + 3, j1 - 3, l1, l1);
         this.drawGradientRect(var14 - 3, j1 + k1 + 3, var14 + k + 3, j1 + k1 + 4, l1, l1);
         this.drawGradientRect(var14 - 3, j1 - 3, var14 + k + 3, j1 + k1 + 3, l1, l1);
         this.drawGradientRect(var14 - 4, j1 - 3, var14 - 3, j1 + k1 + 3, l1, l1);
         this.drawGradientRect(var14 + k + 3, j1 - 3, var14 + k + 4, j1 + k1 + 3, l1, l1);
         int i2 = 1347420415;
         int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
         this.drawGradientRect(var14 - 3, j1 - 3 + 1, var14 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
         this.drawGradientRect(var14 + k + 2, j1 - 3 + 1, var14 + k + 3, j1 + k1 + 3 - 1, i2, j2);
         this.drawGradientRect(var14 - 3, j1 - 3, var14 + k + 3, j1 - 3 + 1, i2, i2);
         this.drawGradientRect(var14 - 3, j1 + k1 + 2, var14 + k + 3, j1 + k1 + 3, j2, j2);

         for(int k2 = 0; k2 < par1List.size(); ++k2) {
            String s1 = (String)par1List.get(k2);
            super.fontRendererObj.drawStringWithShadow(s1, var14, j1, -1);
            if(k2 == 0) {
               j1 += 2;
            }

            j1 += 10;
         }

         super.zLevel = 0.0F;
         GL11.glEnable(2929);
         GL11.glEnable('\u803a');
      }

   }

   public void drawPlayerOnGui(int par1, int par2, int par3, float par4, float par5) {
      if(this.player != null) {
         CommonProxy var10001 = Hats.proxy;
         this.hat = (EntityHat)CommonProxy.tickHandlerClient.hats.get(this.player.getCommandSenderName());
         if(this.hat == null || this.hat.renderingParent == null) {
            return;
         }

         GL11.glEnable(2903);
         GL11.glPushMatrix();
         GL11.glDisable(3008);
         GL11.glTranslatef((float)par1, (float)par2, 500.0F);
         GL11.glScalef((float)(-par3), (float)par3, (float)par3);
         GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
         float f2 = this.hat.renderingParent.renderYawOffset;
         float f3 = this.hat.renderingParent.rotationYaw;
         float f4 = this.hat.renderingParent.rotationPitch;
         float ff3 = this.hat.rotationYaw;
         float ff4 = this.hat.rotationPitch;
         GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
         RenderHelper.enableStandardItemLighting();
         GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-((float)Math.atan((double)(par5 / 40.0F))) * 20.0F + 1.0E-5F, 1.0F, 0.0F, 0.0F);
         this.hat.renderingParent.renderYawOffset = (float)Math.atan((double)(par4 / 40.0F)) * 20.0F;
         this.hat.renderingParent.rotationYaw = this.hat.rotationYaw = (float)Math.atan((double)(par4 / 40.0F)) * 40.0F;
         this.hat.renderingParent.rotationPitch = this.hat.rotationPitch = -((float)Math.atan((double)(par5 / 40.0F))) * 20.0F;
         this.hat.renderingParent.rotationYawHead = this.hat.renderingParent.rotationYaw;
         float nextEntSize = this.hat.renderingParent.width > this.hat.renderingParent.height?this.hat.renderingParent.width:this.hat.renderingParent.height;
         float nextScaleMag = nextEntSize > 2.5F?2.5F / nextEntSize:1.0F;
         GL11.glScalef(nextScaleMag, nextScaleMag, nextScaleMag);
         GL11.glTranslatef(0.0F, this.hat.parent == this.hat.renderingParent?this.hat.parent.yOffset:0.0F, 0.0F);
         RenderManager.instance.playerViewY = 180.0F;
         RenderManager.instance.renderEntityWithPosYaw(this.hat.renderingParent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
         Tessellator.instance.setBrightness(240);
         int rend = Hats.config.getSessionInt("renderHats");
         Hats.config.updateSession("renderHats", Integer.valueOf(13131));
         CommonProxy var10000 = Hats.proxy;
         CommonProxy.tickHandlerClient.updateHatPosAndAngle(this.hat, this.hat.renderingParent);
         RenderHelper.disableStandardItemLighting();
         int i = 15728880;
         int j = i % 65536;
         int k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
         HatInfoClient info = this.hat.info;
         if(this.tempInfo == null || info == null || !this.tempInfo.hatName.equalsIgnoreCase(this.hat.hatName) || this.tempInfo.colourR != this.hat.getR() || this.tempInfo.colourG != this.hat.getG() || this.tempInfo.colourB != this.hat.getB() || this.tempInfo.alpha != this.hat.getA()) {
            this.tempInfo = new HatInfoClient(this.hat.hatName, this.hat.getR(), this.hat.getG(), this.hat.getB(), this.hat.getA());
         }

         this.hat.info = this.tempInfo;
         RenderManager.instance.renderEntityWithPosYaw(this.hat, 0.0D, this.hat.parent == this.hat.renderingParent?0.0D:(double)this.hat.parent.yOffset, 0.0D, this.hat.rotationYaw, 1.0F);
         this.hat.info = info;
         Hats.config.updateSession("renderHats", Integer.valueOf(rend));
         this.hat.renderingParent.renderYawOffset = f2;
         this.hat.renderingParent.rotationYaw = f3;
         this.hat.renderingParent.rotationPitch = f4;
         this.hat.rotationYaw = ff3;
         this.hat.rotationPitch = ff4;
         GL11.glEnable(3008);
         GL11.glPopMatrix();
         GL11.glDisable('\u803a');
         OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glDisable(3553);
         OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
      }

   }

   public void drawSearchBar() {
      if(this.searchBar.getVisible()) {
         this.searchBar.drawTextBox();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         super.mc.getTextureManager().bindTexture(texIcons);
         this.drawTexturedModalRect(super.width / 2 - 85, super.height - 22, !this.adding && !this.renaming?128:112, 0, 16, 16);
         GL11.glDisable(3042);
         if((this.adding || this.renaming) && this.searchBar.getText().equalsIgnoreCase("")) {
            super.fontRendererObj.drawString("Category Name?", super.width / 2 - 61, super.height - 18, 11184810);
         }
      }

   }

   public void onChangeSliderValue(GuiSlider slider) {
		if(slider.id == 5)
		{
			colourR = (int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue);
			hat.setR(colourR);
		}
		else if(slider.id == 6)
		{
			colourG = (int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue);
			hat.setG(colourG);
		}
		else if(slider.id == 7)
		{
			colourB = (int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue);
			hat.setB(colourB);
		}
        else if(slider.id == 29)
        {
            alpha = (int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue);
            hat.setA(alpha);
        }
		else if(slider.id == ID_MOB_SLIDER)
		{
			randoMob = (int)Math.round(slider.sliderValue * (slider.maxValue - slider.minValue) + slider.minValue);
		}
   }

   private static String getHelpHeader() {
      return "§cProtip! (" + Integer.toString(helpPage + 1) + "/" + Integer.toString(help.size()) + ")";
   }

   private static List getCurrentHelpText() {
      ArrayList list = new ArrayList();
      list.add(getHelpHeader());
      list.add("");
      String[] str = (String[])help.get(helpPage);

      for(int i = 0; i < str.length; ++i) {
         list.add(str[i]);
      }

      return list;
   }

   static {
      help.add(helpInfo1);
      help.add(helpInfo2);
      help.add(helpInfo3);
      help.add(helpInfo4);
      help.add(helpInfo5);
      help.add(helpInfo6);
      help.add(helpInfo7);
      help.add(helpInfo8);
      help.add(helpInfo9);
      help.add(helpInfo10);
      help.add(helpInfo11);
      help.add(helpInfo12);
      help.add(helpInfo13);
      help.add(helpInfo14);
      help.add(helpInfo15);
      help.add(helpInfo16);
      help.add(helpInfo17);
      help.add(helpInfo18);
      help.add(helpInfo19);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      helpPage = calendar.get(5) % help.size();
   }
}
