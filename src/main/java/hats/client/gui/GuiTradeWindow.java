package hats.client.gui;

import hats.client.core.HatInfoClient;
import hats.client.render.HatRendererHelper;
import hats.common.Hats;
import hats.common.core.CommonProxy;
import hats.common.core.HatHandler;
import hats.common.entity.EntityHat;
import hats.common.packet.PacketPing;
import hats.common.packet.PacketString;
import hats.common.packet.PacketTradeOffers;
import ichun.client.gui.GuiSlider;
import ichun.client.gui.ISlider;
import ichun.client.render.RendererHelper;
import ichun.common.core.network.PacketHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTradeWindow extends GuiScreen implements ISlider {

   public static final ResourceLocation texIcons = new ResourceLocation("hats", "textures/gui/icons.png");
   public static final ResourceLocation texTradeWindow = new ResourceLocation("hats", "textures/gui/tradewindow.png");
   private final int ID_TOGGLE_HATINV = 1;
   private final int ID_SLIDER_INV = 2;
   private final int ID_MAKE_READY = 3;
   private final int ID_MAKE_TRADE = 4;
   protected int xSize = 256;
   protected int ySize = 230;
   public float mouseX;
   public float mouseY;
   protected int guiLeft;
   protected int guiTop;
   public ArrayList items;
   public TreeMap hats;
   public ArrayList ourItemsForTrade;
   public TreeMap ourHatsForTrade;
   public ArrayList theirItemsForTrade;
   public TreeMap theirHatsForTrade;
   public ItemStack grabbedStack;
   public int hatSlots = 3;
   public int invSlots = 12;
   public double sliderProg = 0.0D;
   public RenderItem itemRenderer = new RenderItem();
   public boolean selfCanScroll = false;
   public boolean theirCanScroll = false;
   public boolean selfIsScrolling = false;
   public boolean theirIsScrolling = false;
   public float selfScrollProg;
   public float theirScrollProg;
   public boolean selfReady;
   public boolean theirReady;
   public String lastClicked = "";
   public int clickTimeout;
   public Minecraft field_146297_k;
   public boolean showInv;
   public long rotationalClock;
   public String trader;
   public GuiTextField searchBar;
   public GuiTextField chatBar;
   public ArrayList chatMessages;
   public float chatScroll;
   public boolean chatScrolling;
   public boolean updateOffers;
   public boolean pointOfNoReturn;
   public boolean clickedMakeTrade;


   public GuiTradeWindow(String trader1) {
      this.trader = trader1;
      this.field_146297_k = Minecraft.getMinecraft();
      this.showInv = false;
      this.items = new ArrayList();
      TreeMap var10001 = new TreeMap(CommonProxy.tickHandlerClient.availableHats);
      CommonProxy var10003 = Hats.proxy;
      this.hats = var10001;
      this.invSlots = 0;
      ItemStack[] hatEnt = this.field_146297_k.thePlayer.inventory.mainInventory;
      int ite = hatEnt.length;

      for(int e = 0; e < ite; ++e) {
         ItemStack is = hatEnt[e];
         if(is != null) {
            this.items.add(is.copy());
            ++this.invSlots;
         }
      }

      if(this.invSlots < 12) {
         this.invSlots = 12;
      }

      CommonProxy var10000 = Hats.proxy;
      EntityHat var6 = (EntityHat)CommonProxy.tickHandlerClient.hats.get(this.field_146297_k.thePlayer.getCommandSenderName());
      this.hatSlots = 0;
      Iterator var7 = this.hats.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         if(!HatHandler.isPlayersContributorHat((String)var8.getKey(), this.field_146297_k.thePlayer.getCommandSenderName()) && (var6 == null || !((String)var8.getKey()).equalsIgnoreCase(var6.info.hatName))) {
            ++this.hatSlots;
         } else {
            var8.setValue(Integer.valueOf(((Integer)var8.getValue()).intValue() - 1));
            if(((Integer)var8.getValue()).intValue() <= 0) {
               var7.remove();
            } else {
               ++this.hatSlots;
            }
         }
      }

      if(this.hatSlots < 3) {
         this.hatSlots = 3;
      }

      this.ourItemsForTrade = new ArrayList();
      this.ourHatsForTrade = new TreeMap();
      this.theirItemsForTrade = new ArrayList();
      this.theirHatsForTrade = new TreeMap();
      this.chatMessages = new ArrayList();
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.rotationalClock = Minecraft.getSystemTime();
      this.guiLeft = (super.width - this.xSize) / 2;
      this.guiTop = (super.height - this.ySize) / 2;
      super.buttonList.clear();
      super.buttonList.add(new GuiButton(1, this.guiLeft + 6, this.guiTop + 6, 108, 20, this.showInv?StatCollector.translateToLocal("hats.trade.yourInventory"):StatCollector.translateToLocal("hats.trade.yourHats")));
      super.buttonList.add(new GuiSlider(2, this.guiLeft + 6, this.guiTop + 65, 108, "", "", 0.0D, 1.0D, this.sliderProg, false, false, this));
      super.buttonList.add(new GuiButton(3, this.guiLeft + 128, this.guiTop + 77, 120, 20, ""));
      super.buttonList.add(new GuiButton(4, this.guiLeft + 148, this.guiTop + this.ySize - 32, 80, 20, StatCollector.translateToLocal("hats.trade.makeTrade")));
      this.searchBar = new GuiTextField(this.field_146297_k.fontRenderer, this.guiLeft + 21, this.guiTop + 90, 93, this.field_146297_k.fontRenderer.FONT_HEIGHT);
      this.searchBar.setMaxStringLength(15);
      this.searchBar.setEnableBackgroundDrawing(false);
      this.searchBar.setTextColor(16777215);
      this.chatBar = new GuiTextField(this.field_146297_k.fontRenderer, this.guiLeft + 6, this.guiTop + this.ySize - 15, 103, this.field_146297_k.fontRenderer.FONT_HEIGHT);
      this.chatBar.setMaxStringLength(80);
      this.chatBar.setEnableBackgroundDrawing(false);
      this.chatBar.setTextColor(16777215);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      PacketHandler.sendToServer(Hats.channels, new PacketPing(2, false));
   }

   public void updateScreen() {
      if(this.updateOffers) {
         this.updateOffers = false;
         PacketHandler.sendToServer(Hats.channels, new PacketTradeOffers(this.ourHatsForTrade, this.ourItemsForTrade));
      }

      if(this.clickTimeout > 0) {
         --this.clickTimeout;
         if(this.clickTimeout == 0) {
            this.lastClicked = "";
         }
      }

      this.searchBar.updateCursorCounter();
   }

   public boolean handleClickStack(ItemStack is, ArrayList refList, int btn) {
      int tradeSize = this.ourItemsForTrade.size();
      boolean flag = false;
      boolean scroll = true;
      if(this.grabbedStack == null) {
         if(is != null) {
            if(btn == 0) {
               this.grabbedStack = is;
               if(GuiScreen.isShiftKeyDown()) {
                  ArrayList ii = refList == this.items?this.ourItemsForTrade:this.items;
                  this.handleClickStack(is, ii, btn);
                  scroll = false;
               }

               refList.remove(is);
               this.updateOffers = true;
            } else if(btn == 1) {
               ItemStack var10 = is.splitStack(is.stackSize / 2);
               if(var10.stackSize == 0) {
                  this.grabbedStack = is;
                  refList.remove(is);
               } else {
                  this.grabbedStack = var10;
               }

               if(is.stackSize <= 0) {
                  refList.remove(is);
               }

               this.updateOffers = true;
            }
         }
      } else {
         boolean var11 = false;
         Iterator btn1 = refList.iterator();

         while(btn1.hasNext()) {
            ItemStack is1 = (ItemStack)btn1.next();
            if(is1.isItemEqual(is) && ItemStack.areItemStackTagsEqual(is, is1) && is1.stackSize < is1.getMaxStackSize() && is.stackSize > 0) {
               if(btn == 0) {
                  while(is1.stackSize < is1.getMaxStackSize() && is.stackSize > 0) {
                     ++is1.stackSize;
                     --is.stackSize;
                  }
               } else if(btn == 1) {
                  ++is1.stackSize;
                  --is.stackSize;
                  var11 = true;
               }
            }
         }

         if(is.stackSize <= 0) {
            this.grabbedStack = null;
         } else if(btn == 0) {
            refList.add(is);
            this.grabbedStack = null;
         } else if(btn == 1 && !var11) {
            refList.add(is.splitStack(1));
            if(is.stackSize <= 0) {
               this.grabbedStack = null;
            }
         }

         this.updateOffers = true;
         flag = true;
      }

      this.selfCanScroll = this.ourHatsForTrade.size() > 3 || this.ourItemsForTrade.size() > 6;
      if(!this.selfCanScroll) {
         this.selfScrollProg = 0.0F;
      } else if(scroll && tradeSize != this.ourItemsForTrade.size() && (this.ourItemsForTrade.size() % 6 == 1 && tradeSize % 6 == 0 || this.ourItemsForTrade.size() % 6 == 0 && tradeSize % 6 == 1)) {
         float var13 = (float)Math.ceil((double)((float)Math.max(this.ourHatsForTrade.size(), 3) / 3.0F)) * 2.0F + (float)Math.ceil((double)((float)Math.max(tradeSize, 6) / 6.0F)) - 3.0F;
         if(var13 > 0.0F) {
            this.selfScrollProg = MathHelper.clamp_float(this.selfScrollProg * (this.ourItemsForTrade.size() > tradeSize?var13 / (var13 + 1.0F):var13 / (var13 - 1.0F)), 0.0F, 1.0F);
         }
      }

      if(this.items.size() <= 12 && this.showInv) {
         for(int var12 = 0; var12 < super.buttonList.size(); ++var12) {
            GuiButton var14 = (GuiButton)super.buttonList.get(var12);
            if(var14 instanceof GuiSlider) {
               ((GuiSlider)var14).sliderValue = 0.0D;
               ((GuiSlider)var14).updateSlider();
            }
         }
      }

      return flag;
   }

   protected void mouseClicked(int x, int y, int btn) {
      super.mouseClicked(x, y, btn);
      boolean isOnOurScroll;
      boolean var24;
      if(!this.selfReady) {
         isOnOurScroll = x >= this.guiLeft + 6 && x < this.guiLeft + 6 + 108 && y >= this.guiTop + 29 && y < this.guiTop + 29 + 36;
         int i;
         String hatName;
         int var37;
         int var42;
         int var41;
         Entry var46;
         Iterator var47;
         if(isOnOurScroll && !this.handleClickStack(this.grabbedStack, this.items, btn)) {
            int isOnTheirScroll = x - (this.guiLeft + 6);
            ArrayList isOnChatScroll = new ArrayList(this.items);
            TreeMap isOnSearchBar = new TreeMap(this.hats);
            Iterator isOnChatBar = isOnSearchBar.entrySet().iterator();

            while(isOnChatBar.hasNext()) {
               Entry slotsToDraw = (Entry)isOnChatBar.next();
               Iterator hatLevels = this.ourHatsForTrade.entrySet().iterator();

               while(hatLevels.hasNext()) {
                  Entry boxes = (Entry)hatLevels.next();
                  if(((String)slotsToDraw.getKey()).equalsIgnoreCase((String)boxes.getKey())) {
                     slotsToDraw.setValue(Integer.valueOf(((Integer)slotsToDraw.getValue()).intValue() - ((Integer)boxes.getValue()).intValue()));
                     if(((Integer)slotsToDraw.getValue()).intValue() <= 0) {
                        isOnChatBar.remove();
                        break;
                     }
                  }
               }
            }

            String var31 = this.searchBar.getText();
            boolean overallLength;
            String[] startY;
            int var44;
            if(this.showInv) {
               for(var37 = isOnChatScroll.size() - 1; var37 >= 0; --var37) {
                  ItemStack var35 = (ItemStack)isOnChatScroll.get(var37);
                  if(!var35.getDisplayName().toLowerCase().startsWith(var31.toLowerCase())) {
                     overallLength = true;
                     startY = var35.getDisplayName().split(" ");
                     String[] clicked = startY;
                     i = startY.length;

                     for(int ite = 0; ite < i; ++ite) {
                        String e = clicked[ite];
                        if(e.toLowerCase().startsWith(var31.toLowerCase())) {
                           overallLength = false;
                        }
                     }

                     if(overallLength) {
                        isOnChatScroll.remove(var37);
                     }
                  }
               }
            } else {
               isOnChatBar = isOnSearchBar.entrySet().iterator();

               while(isOnChatBar.hasNext()) {
                  Entry var34 = (Entry)isOnChatBar.next();
                  if(!((String)var34.getKey()).toLowerCase().startsWith(var31.toLowerCase())) {
                     String[] var39 = ((String)var34.getKey()).split(" ");
                     overallLength = true;
                     startY = var39;
                     var44 = var39.length;

                     for(i = 0; i < var44; ++i) {
                        String var45 = startY[i];
                        if(var45.toLowerCase().startsWith(var31.toLowerCase())) {
                           overallLength = false;
                        }
                     }

                     if(overallLength) {
                        isOnChatBar.remove();
                     }
                  }
               }
            }

            this.invSlots = isOnChatScroll.size();
            this.hatSlots = isOnSearchBar.size();
            if(this.invSlots < 12) {
               this.invSlots = 12;
            }

            if(this.hatSlots < 3) {
               this.hatSlots = 3;
            }

            var37 = this.showInv?18:36;
            byte var40 = 108;
            var42 = this.showInv?(int)Math.ceil((double)((float)this.invSlots / 2.0F)):this.hatSlots;
            var41 = var42 * var37;
            var44 = this.guiLeft + 6 + (int)((double)(var41 - var40) * this.sliderProg);
            if(this.showInv) {
               for(i = 0; i < isOnChatScroll.size(); ++i) {
                  if(this.guiLeft + 6 + var37 * (int)Math.floor((double)((float)i / 2.0F)) + var37 >= var44 && this.guiLeft + 6 + var37 * (int)Math.floor((double)((float)i / 2.0F)) <= var44 + isOnTheirScroll && this.guiLeft + 6 + var37 * (int)Math.floor((double)((float)i / 2.0F)) + var37 > var44 + isOnTheirScroll && (y < this.guiTop + 29 + var37 && i % 2 == 0 || y >= this.guiTop + 29 + var37 && i % 2 == 1)) {
                     this.handleClickStack((ItemStack)isOnChatScroll.get(i), this.items, btn);
                     break;
                  }
               }
            } else if(btn == 0) {
               i = 0;
               var47 = isOnSearchBar.entrySet().iterator();

               while(var47.hasNext()) {
                  var46 = (Entry)var47.next();
                  if(this.guiLeft + 6 + var37 * i + var37 < var44) {
                     ++i;
                  } else {
                     if(this.guiLeft + 6 + var37 * i <= var44 + isOnTheirScroll && this.guiLeft + 6 + var37 * i + var37 > var44 + isOnTheirScroll) {
                        hatName = (String)var46.getKey();
                        if(hatName.equalsIgnoreCase(this.lastClicked)) {
                           boolean hatsList = this.ourHatsForTrade.get(hatName) == null;
                           this.ourHatsForTrade.put(hatName, Integer.valueOf(hatsList?1:((Integer)this.ourHatsForTrade.get(hatName)).intValue() + 1));
                           this.selfCanScroll = this.ourHatsForTrade.size() > 3 || this.ourItemsForTrade.size() > 6;
                           if(!this.selfCanScroll) {
                              this.selfScrollProg = 0.0F;
                           } else if(this.ourHatsForTrade.size() % 3 == 1) {
                              float ite1 = (float)Math.floor((double)((float)Math.max(this.ourHatsForTrade.size(), 3) / 3.0F)) * 2.0F + (float)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F)) - 3.0F;
                              if(ite1 > 0.0F) {
                                 this.selfScrollProg = this.selfScrollProg * ite1 / (ite1 + 2.0F);
                              }
                           }

                           if(((Integer)var46.getValue()).intValue() == 1) {
                              for(int var49 = 0; var49 < super.buttonList.size(); ++var49) {
                                 GuiButton ii = (GuiButton)super.buttonList.get(var49);
                                 if(ii instanceof GuiSlider) {
                                    ((GuiSlider)ii).sliderValue = (double)MathHelper.clamp_float((float)((GuiSlider)ii).sliderValue * (float)(this.hatSlots - 3) / (float)(this.hatSlots - 4), 0.0F, 1.0F);
                                    ((GuiSlider)ii).updateSlider();
                                 }
                              }
                           }

                           this.lastClicked = "";
                           this.updateOffers = true;
                        } else {
                           this.lastClicked = hatName;
                        }
                        break;
                     }

                     ++i;
                  }
               }
            }
         }

         var24 = x >= this.guiLeft + 125 && x < this.guiLeft + 125 + 108 && y >= this.guiTop + 17 && y < this.guiTop + 17 + 54;
         if(var24 && !this.handleClickStack(this.grabbedStack, this.ourItemsForTrade, btn)) {
            int var26 = y - (this.guiTop + 17);
            byte var27 = 36;
            byte var29 = 54;

            int var33;
            for(var33 = this.ourHatsForTrade.size(); var33 % 3 != 0 || var33 < 3; ++var33) {
               ;
            }

            var37 = (int)Math.ceil((double)((float)var33 / 3.0F));
            int var38 = var37 * 2 + (int)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F));
            var42 = var38 * 18;
            var41 = this.guiTop + 17 + (int)((float)(var42 - var29) * this.selfScrollProg);
            boolean var43 = false;
            if(btn == 0) {
               i = 0;
               var47 = this.ourHatsForTrade.entrySet().iterator();

               while(var47.hasNext()) {
                  var46 = (Entry)var47.next();
                  if(this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 3.0F)) + var27 < var41) {
                     ++i;
                  } else {
                     if(this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 3.0F)) <= var41 + var26 && this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 3.0F)) + var27 > var41 + var26 && (x < this.guiLeft + 125 + var27 && i % 3 == 0 || x < this.guiLeft + 125 + var27 + var27 && i % 3 == 1 || x >= this.guiLeft + 125 + var27 + var27 && i % 3 == 2)) {
                        hatName = (String)var46.getKey();
                        if(hatName.equalsIgnoreCase(this.lastClicked)) {
                           var46.setValue(Integer.valueOf(((Integer)var46.getValue()).intValue() - 1));
                           if(((Integer)var46.getValue()).intValue() <= 0) {
                              var47.remove();
                           }

                           this.selfCanScroll = this.ourHatsForTrade.size() > 3 || this.ourItemsForTrade.size() > 6;
                           if(!this.selfCanScroll) {
                              this.selfScrollProg = 0.0F;
                           } else if(this.ourHatsForTrade.size() % 3 == 0) {
                              float var48 = (float)Math.floor((double)((float)Math.max(this.ourHatsForTrade.size(), 3) / 3.0F)) * 2.0F + (float)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F)) - 3.0F;
                              if(var48 > 0.0F) {
                                 this.selfScrollProg = MathHelper.clamp_float(this.selfScrollProg * (var48 + 2.0F) / var48, 0.0F, 1.0F);
                              }
                           }

                           if(!this.showInv) {
                              TreeMap var50 = new TreeMap(this.hats);
                              Iterator var52 = var50.entrySet().iterator();

                              while(var52.hasNext()) {
                                 Entry var53 = (Entry)var52.next();
                                 Iterator btn1 = this.ourHatsForTrade.entrySet().iterator();

                                 while(btn1.hasNext()) {
                                    Entry e1 = (Entry)btn1.next();
                                    if(((String)var53.getKey()).equalsIgnoreCase((String)e1.getKey())) {
                                       var53.setValue(Integer.valueOf(((Integer)var53.getValue()).intValue() - ((Integer)e1.getValue()).intValue()));
                                       if(((Integer)var53.getValue()).intValue() <= 0) {
                                          var52.remove();
                                          break;
                                       }
                                    }
                                 }
                              }

                              if(((Integer)var50.get(hatName)).intValue() == 1) {
                                 for(int var54 = 0; var54 < super.buttonList.size(); ++var54) {
                                    GuiButton var51 = (GuiButton)super.buttonList.get(var54);
                                    if(var51 instanceof GuiSlider) {
                                       ((GuiSlider)var51).sliderValue = (double)MathHelper.clamp_float((float)((GuiSlider)var51).sliderValue * (float)(this.hatSlots - 3) / (float)(this.hatSlots + 1 - 3), 0.0F, 1.0F);
                                       ((GuiSlider)var51).updateSlider();
                                    }
                                 }
                              }
                           }

                           this.lastClicked = "";
                           this.updateOffers = true;
                        } else {
                           this.lastClicked = hatName;
                        }

                        var43 = true;
                        break;
                     }

                     ++i;
                  }
               }
            }

            var27 = 18;
            int var32 = 108 / var27;
            boolean var36 = true;
            if(!var43) {
               for(i = 0; i < this.ourItemsForTrade.size(); ++i) {
                  if(this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 6.0F)) + var27 + var37 * 36 >= var41 && this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 6.0F)) + var37 * 36 <= var41 + var26 && this.guiTop + 17 + var27 * (int)Math.floor((double)((float)i / 6.0F)) + var27 + var37 * 36 > var41 + var26 && x < this.guiLeft + 125 + var27 + i % 6 * var27) {
                     this.handleClickStack((ItemStack)this.ourItemsForTrade.get(i), this.ourItemsForTrade, btn);
                     break;
                  }
               }
            }
         }
      }

      if(this.grabbedStack == null) {
         isOnOurScroll = x >= this.guiLeft + 237 && x < this.guiLeft + 237 + 12 && y >= this.guiTop + 18 && y < this.guiTop + 18 + 37 + 15;
         if(isOnOurScroll && btn == 0 && this.selfCanScroll) {
            this.selfIsScrolling = true;
         }

         var24 = x >= this.guiLeft + 237 && x < this.guiLeft + 237 + 12 && y >= this.guiTop + 117 && y < this.guiTop + 117 + 37 + 15;
         if(var24 && btn == 0 && this.theirCanScroll) {
            this.theirIsScrolling = true;
         }

         boolean var25 = x >= this.guiLeft + 107 && x < this.guiLeft + 107 + 4 && y >= this.guiTop + 195 - 82 && y < this.guiTop + 195 + 15;
         if(var25) {
            this.chatScrolling = true;
         }

         boolean var28 = x >= this.guiLeft + 21 && x < this.guiLeft + 21 + 93 && y >= this.guiTop + 90 && y < this.guiTop + 90 + this.field_146297_k.fontRenderer.FONT_HEIGHT;
         this.searchBar.setFocused(var28);
         boolean var30 = x >= this.guiLeft + 6 && x < this.guiLeft + 6 + 106 && y >= this.guiTop + this.ySize - 15 && y < this.guiTop + this.ySize - 15 + this.field_146297_k.fontRenderer.FONT_HEIGHT;
         this.chatBar.setFocused(var30);
      }

   }

   protected void keyTyped(char c, int i) {
      this.searchBar.textboxKeyTyped(c, i);
      this.chatBar.textboxKeyTyped(c, i);
      if(this.searchBar.isFocused()) {
         for(int ii = 0; ii < super.buttonList.size(); ++ii) {
            GuiButton btn1 = (GuiButton)super.buttonList.get(ii);
            if(btn1 instanceof GuiSlider) {
               ((GuiSlider)btn1).sliderValue = 0.0D;
               ((GuiSlider)btn1).updateSlider();
            }
         }
      }

      if(this.chatBar.isFocused() && i == 28 && !this.chatBar.getText().isEmpty()) {
         PacketHandler.sendToServer(Hats.channels, new PacketString(2, this.field_146297_k.thePlayer.getCommandSenderName() + ": " + this.chatBar.getText()));
         this.chatMessages.add(this.field_146297_k.thePlayer.getCommandSenderName() + ": " + this.chatBar.getText());
         this.chatBar.setText("");
      }

      if(i == 1) {
         if(this.searchBar.isFocused()) {
            this.searchBar.setText("");
            this.searchBar.setFocused(false);
         } else {
            this.field_146297_k.displayGuiScreen((GuiScreen)null);
            this.field_146297_k.setIngameFocus();
         }
      }

   }

   public void drawForeground(int x, int y, float par3) {
      if(this.grabbedStack != null) {
         this.drawItemStack(this.grabbedStack, x - 8, y - 8);
      } else {
         boolean isInInv = x >= this.guiLeft + 6 && x < this.guiLeft + 6 + 108 && y >= this.guiTop + 29 && y < this.guiTop + 29 + 36;
         boolean isInSelf = x >= this.guiLeft + 125 && x < this.guiLeft + 125 + 108 && y >= this.guiTop + 17 && y < this.guiTop + 17 + 54;
         boolean isInThem = x >= this.guiLeft + 125 && x < this.guiLeft + 125 + 108 && y >= this.guiTop + 116 && y < this.guiTop + 116 + 54;
         int mouseProg;
         int i;
         String hatName;
         int var26;
         int var34;
         int var32;
         if(isInInv) {
            mouseProg = x - (this.guiLeft + 6);
            ArrayList size = new ArrayList(this.items);
            TreeMap columnWidth = new TreeMap(this.hats);
            Iterator slotsToDraw = columnWidth.entrySet().iterator();

            while(slotsToDraw.hasNext()) {
               Entry hatLevels = (Entry)slotsToDraw.next();
               Iterator boxes = this.ourHatsForTrade.entrySet().iterator();

               while(boxes.hasNext()) {
                  Entry overallLength = (Entry)boxes.next();
                  if(((String)hatLevels.getKey()).equalsIgnoreCase((String)overallLength.getKey())) {
                     hatLevels.setValue(Integer.valueOf(((Integer)hatLevels.getValue()).intValue() - ((Integer)overallLength.getValue()).intValue()));
                     if(((Integer)hatLevels.getValue()).intValue() <= 0) {
                        slotsToDraw.remove();
                        break;
                     }
                  }
               }
            }

            String var24 = this.searchBar.getText();
            boolean startY;
            String[] clicked;
            if(this.showInv) {
               for(var26 = size.size() - 1; var26 >= 0; --var26) {
                  ItemStack var31 = (ItemStack)size.get(var26);
                  if(!var31.getDisplayName().toLowerCase().startsWith(var24.toLowerCase())) {
                     startY = true;
                     clicked = var31.getDisplayName().split(" ");
                     String[] ii = clicked;
                     i = clicked.length;

                     for(int e = 0; e < i; ++e) {
                        hatName = ii[e];
                        if(hatName.toLowerCase().startsWith(var24.toLowerCase())) {
                           startY = false;
                        }
                     }

                     if(startY) {
                        size.remove(var26);
                     }
                  }
               }
            } else {
               slotsToDraw = columnWidth.entrySet().iterator();

               while(slotsToDraw.hasNext()) {
                  Entry var30 = (Entry)slotsToDraw.next();
                  if(!((String)var30.getKey()).toLowerCase().startsWith(var24.toLowerCase())) {
                     String[] var28 = ((String)var30.getKey()).split(" ");
                     startY = true;
                     clicked = var28;
                     var34 = var28.length;

                     for(i = 0; i < var34; ++i) {
                        String var40 = clicked[i];
                        if(var40.toLowerCase().startsWith(var24.toLowerCase())) {
                           startY = false;
                        }
                     }

                     if(startY) {
                        slotsToDraw.remove();
                     }
                  }
               }
            }

            this.invSlots = size.size();
            this.hatSlots = columnWidth.size();
            if(this.invSlots < 12) {
               this.invSlots = 12;
            }

            if(this.hatSlots < 3) {
               this.hatSlots = 3;
            }

            var26 = this.showInv?18:36;
            byte var29 = 108;
            var32 = this.showInv?(int)Math.ceil((double)((float)this.invSlots / 2.0F)):this.hatSlots;
            int var36 = var32 * var26;
            var34 = this.guiLeft + 6 + (int)((double)(var36 - var29) * this.sliderProg);
            if(this.showInv) {
               for(i = 0; i < size.size(); ++i) {
                  if(this.guiLeft + 6 + var26 * (int)Math.floor((double)((float)i / 2.0F)) + var26 >= var34 && this.guiLeft + 6 + var26 * (int)Math.floor((double)((float)i / 2.0F)) <= var34 + mouseProg && this.guiLeft + 6 + var26 * (int)Math.floor((double)((float)i / 2.0F)) + var26 > var34 + mouseProg && (y < this.guiTop + 29 + var26 && i % 2 == 0 || y >= this.guiTop + 29 + var26 && i % 2 == 1)) {
                     this.drawTooltip(((ItemStack)size.get(i)).getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips), x, y);
                     break;
                  }
               }
            } else {
               i = 0;
               Iterator var39 = columnWidth.entrySet().iterator();

               while(var39.hasNext()) {
                  Entry var38 = (Entry)var39.next();
                  if(this.guiLeft + 6 + var26 * i + var26 < var34) {
                     ++i;
                  } else {
                     if(this.guiLeft + 6 + var26 * i <= var34 + mouseProg && this.guiLeft + 6 + var26 * i + var26 > var34 + mouseProg) {
                        this.drawTooltip(Arrays.asList(new String[]{(String)var38.getKey()}), x, y);
                        break;
                     }

                     ++i;
                  }
               }
            }
         }

         byte var21;
         byte var20;
         boolean var23;
         int var22;
         int var25;
         int var27;
         boolean var35;
         int var33;
         Entry var37;
         Iterator var41;
         if(isInSelf) {
            mouseProg = y - (this.guiTop + 17);
            var20 = 36;
            var21 = 54;

            for(var25 = this.ourHatsForTrade.size(); var25 % 3 != 0 || var25 < 3; ++var25) {
               ;
            }

            var27 = (int)Math.ceil((double)((float)var25 / 3.0F));
            var26 = var27 * 2 + (int)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F));
            var33 = var26 * 18;
            var32 = this.guiTop + 17 + (int)((float)(var33 - var21) * this.selfScrollProg);
            var35 = false;
            var34 = 0;
            var41 = this.ourHatsForTrade.entrySet().iterator();

            while(var41.hasNext()) {
               var37 = (Entry)var41.next();
               if(this.guiTop + 17 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) + var20 < var32) {
                  ++var34;
               } else {
                  if(this.guiTop + 17 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) <= var32 + mouseProg && this.guiTop + 17 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) + var20 > var32 + mouseProg && (x < this.guiLeft + 125 + var20 && var34 % 3 == 0 || x < this.guiLeft + 125 + var20 + var20 && var34 % 3 == 1 || x >= this.guiLeft + 125 + var20 + var20 && var34 % 3 == 2)) {
                     hatName = (String)var37.getKey();
                     this.drawTooltip(Arrays.asList(new String[]{hatName}), x, y);
                     var35 = true;
                     break;
                  }

                  ++var34;
               }
            }

            var20 = 18;
            var22 = 108 / var20;
            var23 = true;
            if(!var35) {
               for(i = 0; i < this.ourItemsForTrade.size(); ++i) {
                  if(this.guiTop + 17 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var20 + var27 * 36 >= var32 && this.guiTop + 17 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var27 * 36 <= var32 + mouseProg && this.guiTop + 17 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var20 + var27 * 36 > var32 + mouseProg && x < this.guiLeft + 125 + var20 + i % 6 * var20) {
                     this.drawTooltip(((ItemStack)this.ourItemsForTrade.get(i)).getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips), x, y);
                     break;
                  }
               }
            }
         }

         if(isInThem) {
            mouseProg = y - (this.guiTop + 116);
            var20 = 36;
            var21 = 54;

            for(var25 = this.theirHatsForTrade.size(); var25 % 3 != 0 || var25 < 3; ++var25) {
               ;
            }

            var27 = (int)Math.ceil((double)((float)var25 / 3.0F));
            var26 = var27 * 2 + (int)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F));
            var33 = var26 * 18;
            var32 = this.guiTop + 116 + (int)((float)(var33 - var21) * this.theirScrollProg);
            var35 = false;
            var34 = 0;
            var41 = this.theirHatsForTrade.entrySet().iterator();

            while(var41.hasNext()) {
               var37 = (Entry)var41.next();
               if(this.guiTop + 116 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) + var20 < var32) {
                  ++var34;
               } else {
                  if(this.guiTop + 116 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) <= var32 + mouseProg && this.guiTop + 116 + var20 * (int)Math.floor((double)((float)var34 / 3.0F)) + var20 > var32 + mouseProg && (x < this.guiLeft + 125 + var20 && var34 % 3 == 0 || x < this.guiLeft + 125 + var20 + var20 && var34 % 3 == 1 || x >= this.guiLeft + 125 + var20 + var20 && var34 % 3 == 2)) {
                     hatName = (String)var37.getKey();
                     this.drawTooltip(Arrays.asList(new String[]{hatName}), x, y);
                     var35 = true;
                     break;
                  }

                  ++var34;
               }
            }

            var20 = 18;
            var22 = 108 / var20;
            var23 = true;
            if(!var35) {
               for(i = 0; i < this.theirItemsForTrade.size(); ++i) {
                  if(this.guiTop + 116 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var20 + var27 * 36 >= var32 && this.guiTop + 116 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var27 * 36 <= var32 + mouseProg && this.guiTop + 116 + var20 * (int)Math.floor((double)((float)i / 6.0F)) + var20 + var27 * 36 > var32 + mouseProg && x < this.guiLeft + 125 + var20 + i % 6 * var20) {
                     this.drawTooltip(((ItemStack)this.theirItemsForTrade.get(i)).getTooltip(this.field_146297_k.thePlayer, this.field_146297_k.gameSettings.advancedItemTooltips), x, y);
                     break;
                  }
               }
            }
         }
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      if(this.field_146297_k == null) {
         this.field_146297_k = Minecraft.getMinecraft();
         super.fontRendererObj = this.field_146297_k.fontRenderer;
      }

      this.drawDefaultBackground();
      boolean flag = Mouse.isButtonDown(0);
      if(!flag) {
         this.selfIsScrolling = false;
         this.theirIsScrolling = false;
         this.chatScrolling = false;
      } else if(this.selfIsScrolling) {
         this.selfScrollProg = MathHelper.clamp_float((float)(par2 - (this.guiTop + 18 + 7)) / 37.0F, 0.0F, 1.0F);
      } else if(this.theirIsScrolling) {
         this.theirScrollProg = MathHelper.clamp_float((float)(par2 - (this.guiTop + 117 + 7)) / 37.0F, 0.0F, 1.0F);
      } else if(this.chatScrolling) {
         this.chatScroll = MathHelper.clamp_float((float)(this.guiTop + 195 + 7 - par2) / 82.0F, 0.0F, 1.0F);
      }

      this.mouseX = (float)par1;
      this.mouseY = (float)par2;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int k = this.guiLeft;
      int l = this.guiTop;
      ArrayList itemsList = new ArrayList(this.items);
      TreeMap hatsList = new TreeMap(this.hats);
      Iterator ite = hatsList.entrySet().iterator();

      while(ite.hasNext()) {
         Entry query = (Entry)ite.next();
         Iterator size = this.ourHatsForTrade.entrySet().iterator();

         while(size.hasNext()) {
            Entry columnWidth = (Entry)size.next();
            if(((String)query.getKey()).equalsIgnoreCase((String)columnWidth.getKey())) {
               query.setValue(Integer.valueOf(((Integer)query.getValue()).intValue() - ((Integer)columnWidth.getValue()).intValue()));
               if(((Integer)query.getValue()).intValue() <= 0) {
                  ite.remove();
                  break;
               }
            }
         }
      }

      String var22 = this.searchBar.getText();
      boolean slotsToDraw;
      String[] hatLevels;
      int scale;
      int hasItem;
      int var21;
      int var28;
      if(this.showInv) {
         for(var21 = itemsList.size() - 1; var21 >= 0; --var21) {
            ItemStack var23 = (ItemStack)itemsList.get(var21);
            if(!var23.getDisplayName().toLowerCase().startsWith(var22.toLowerCase())) {
               slotsToDraw = true;
               hatLevels = var23.getDisplayName().split(" ");
               String[] boxes = hatLevels;
               hasItem = hatLevels.length;

               for(scale = 0; scale < hasItem; ++scale) {
                  String i = boxes[scale];
                  if(i.toLowerCase().startsWith(var22.toLowerCase())) {
                     slotsToDraw = false;
                  }
               }

               if(slotsToDraw) {
                  itemsList.remove(var21);
               }
            }
         }
      } else {
         ite = hatsList.entrySet().iterator();

         while(ite.hasNext()) {
            Entry var26 = (Entry)ite.next();
            if(!((String)var26.getKey()).toLowerCase().startsWith(var22.toLowerCase())) {
               String[] var25 = ((String)var26.getKey()).split(" ");
               slotsToDraw = true;
               hatLevels = var25;
               var28 = var25.length;

               for(hasItem = 0; hasItem < var28; ++hasItem) {
                  String var32 = hatLevels[hasItem];
                  if(var32.toLowerCase().startsWith(var22.toLowerCase())) {
                     slotsToDraw = false;
                  }
               }

               if(slotsToDraw) {
                  ite.remove();
               }
            }
         }
      }

      this.invSlots = itemsList.size();
      this.hatSlots = hatsList.size();
      if(this.invSlots < 12) {
         this.invSlots = 12;
      }

      if(this.hatSlots < 3) {
         this.hatSlots = 3;
      }

      this.field_146297_k.getTextureManager().bindTexture(texIcons);
      GL11.glPushMatrix();
      RendererHelper.startGlScissor(this.guiLeft + 6, this.guiTop + 29, 108, 36);
      int var27;
      int var29;
      int var30;
      if(this.field_146297_k.thePlayer != null) {
         var21 = this.showInv?18:36;
         var27 = 108 / var21;
         var29 = this.showInv?(int)Math.ceil((double)((float)this.invSlots / 2.0F)):this.hatSlots;
         if(var29 > var27) {
            GL11.glTranslatef(-((float)((double)(var21 * (var29 - var27)) * this.sliderProg)), 0.0F, 0.0F);
         }

         var30 = 0;

         for(var28 = 0; var28 < var29; ++var28) {
            if((float)(var21 * var28 + var21) >= (float)((double)(var21 * (var29 - var27)) * this.sliderProg) && (float)(var21 * var28) <= (float)((double)(var21 * var29) * this.sliderProg + (double)(var27 * var21))) {
               this.drawTexturedModalRect(k + 6 + var21 * var28, l + 29, var21 == 36?0:36, 45, var21, var21);
               if(this.showInv) {
                  if(var30 < itemsList.size()) {
                     this.drawItemStack((ItemStack)itemsList.get(var30), k + 6 + var21 * var28 + 1, l + 29 + 1);
                     ++var30;
                  }
               } else if(var30 < hatsList.size()) {
                  hasItem = 0;

                  for(Iterator var31 = hatsList.entrySet().iterator(); var31.hasNext(); ++hasItem) {
                     Entry var36 = (Entry)var31.next();
                     if(hasItem == var30) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)(k + 6 + var21 * var28 - 2), (float)(l + 29 + 14), -3.0F + super.zLevel);
                        GL11.glScalef(20.0F, 20.0F, 20.0F);
                        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
                        GL11.glScalef(1.0F, 1.0F, -1.0F);
                        GL11.glRotatef(190.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(45.0F + (float)(Minecraft.getSystemTime() - this.rotationalClock) / 6.0F, 0.0F, 1.0F, 0.0F);
                        HatInfoClient btn = new HatInfoClient(((String)var36.getKey()).toLowerCase());
                        HatRendererHelper.renderHat(btn, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, true, true, 1.0F);
                        GL11.glPopMatrix();
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glDisable(2929);
                        this.drawSolidRect(k + 6 + var21 * var28 + var21 - 10, l + 29 + var21 - 10, 9, 9, 0, 0.4F);
                        super.fontRendererObj.drawString(HatHandler.getHatRarityColour((String)var36.getKey()).toString() + (((Integer)var36.getValue()).intValue() > 99?"99":((Integer)var36.getValue()).toString()), k + 6 + var21 * var28 + var21 - 5 - super.fontRendererObj.getStringWidth(((Integer)var36.getValue()).intValue() > 99?"99":((Integer)var36.getValue()).toString()) / 2, l + 29 + var21 - 9, 16777215, true);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glEnable(2929);
                        GL11.glDisable(3042);
                        break;
                     }
                  }

                  ++var30;
               }

               this.field_146297_k.getTextureManager().bindTexture(texIcons);
               if(var21 == 18) {
                  this.drawTexturedModalRect(k + 6 + var21 * var28, l + 29 + var21, var21 == 36?0:36, 45, var21, var21);
                  if(this.showInv && var30 < itemsList.size()) {
                     this.drawItemStack((ItemStack)itemsList.get(var30), k + 6 + var21 * var28 + 1, l + 29 + var21 + 1);
                     ++var30;
                     this.field_146297_k.getTextureManager().bindTexture(texIcons);
                  }
               }
            } else {
               ++var30;
               if(this.showInv) {
                  ++var30;
               }
            }
         }
      }

      GL11.glPopMatrix();
      RendererHelper.startGlScissor(this.guiLeft + 125, this.guiTop + 17, 108, 54);
      byte var24 = 36;
      var27 = 108 / var24;

      for(var29 = this.ourHatsForTrade.size(); var29 % 3 != 0 || var29 < 3; ++var29) {
         ;
      }

      var30 = (int)Math.ceil((double)((float)var29 / 3.0F));
      GL11.glPushMatrix();
      var28 = var30 * 2 + (int)Math.ceil((double)((float)Math.max(this.ourItemsForTrade.size(), 6) / 6.0F));
      GL11.glTranslatef(0.0F, ((float)(-var28) * 18.0F + 54.0F) * this.selfScrollProg, 0.0F);

      HatInfoClient info;
      Iterator var35;
      Entry var33;
      for(hasItem = 0; hasItem < var29; ++hasItem) {
         this.drawTexturedModalRect(k + 125 + var24 * (hasItem % var27), l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)), var24 == 36?0:36, 45, var24, var24);
         if(hasItem < this.ourHatsForTrade.size()) {
            scale = 0;

            for(var35 = this.ourHatsForTrade.entrySet().iterator(); var35.hasNext(); ++scale) {
               var33 = (Entry)var35.next();
               if(scale == hasItem) {
                  GL11.glPushMatrix();
                  GL11.glTranslatef((float)(k + 125 + var24 * (hasItem % var27) - 2), (float)(l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)) + 14), -3.0F + super.zLevel);
                  GL11.glScalef(20.0F, 20.0F, 20.0F);
                  GL11.glTranslatef(1.0F, 0.5F, 1.0F);
                  GL11.glScalef(1.0F, 1.0F, -1.0F);
                  GL11.glRotatef(190.0F, 1.0F, 0.0F, 0.0F);
                  GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                  info = new HatInfoClient(((String)var33.getKey()).toLowerCase());
                  HatRendererHelper.renderHat(info, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, true, true, 1.0F);
                  GL11.glPopMatrix();
                  GL11.glEnable(3042);
                  GL11.glBlendFunc(770, 771);
                  GL11.glDisable(2929);
                  this.drawSolidRect(k + 125 + var24 * (hasItem % var27) + var24 - 10, l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)) + var24 - 10, 9, 9, 0, 0.4F);
                  super.fontRendererObj.drawString(HatHandler.getHatRarityColour((String)var33.getKey()).toString() + (((Integer)var33.getValue()).intValue() > 99?"99":((Integer)var33.getValue()).toString()), k + 125 + var24 * (hasItem % var27) + var24 - 5 - super.fontRendererObj.getStringWidth(((Integer)var33.getValue()).intValue() > 99?"99":((Integer)var33.getValue()).toString()) / 2, l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)) + var24 - 9, 16777215, true);
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glEnable(2929);
                  GL11.glDisable(3042);
                  break;
               }
            }
         }

         this.field_146297_k.getTextureManager().bindTexture(texIcons);
      }

      var24 = 18;
      var27 = 108 / var24;

      for(var29 = this.ourItemsForTrade.size(); var29 % 6 != 0 || var29 < 6; ++var29) {
         ;
      }

      for(hasItem = 0; hasItem < var29; ++hasItem) {
         this.drawTexturedModalRect(k + 125 + var24 * (hasItem % var27), l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)) + 36 * var30, var24 == 36?0:36, 45, var24, var24);
         if(hasItem < this.ourItemsForTrade.size()) {
            this.drawItemStack((ItemStack)this.ourItemsForTrade.get(hasItem), k + 125 + var24 * (hasItem % var27) + 1, l + 17 + var24 * (int)Math.floor((double)(hasItem / var27)) + 36 * var30 + 1);
            this.field_146297_k.getTextureManager().bindTexture(texIcons);
         }
      }

      GL11.glPopMatrix();
      RendererHelper.startGlScissor(this.guiLeft + 125, this.guiTop + 116, 108, 54);
      var24 = 36;
      var27 = 108 / var24;

      for(var29 = this.theirHatsForTrade.size(); var29 % 3 != 0 || var29 < 3; ++var29) {
         ;
      }

      var30 = (int)Math.ceil((double)((float)var29 / 3.0F));
      GL11.glPushMatrix();
      var28 = var30 * 2 + (int)Math.ceil((double)((float)Math.max(this.theirItemsForTrade.size(), 6) / 6.0F));
      GL11.glTranslatef(0.0F, ((float)(-var28) * 18.0F + 54.0F) * this.theirScrollProg, 0.0F);

      for(hasItem = 0; hasItem < var29; ++hasItem) {
         this.drawTexturedModalRect(k + 125 + var24 * (hasItem % var27), l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)), var24 == 36?0:36, 45, var24, var24);
         if(hasItem < this.theirHatsForTrade.size()) {
            scale = 0;

            for(var35 = this.theirHatsForTrade.entrySet().iterator(); var35.hasNext(); ++scale) {
               var33 = (Entry)var35.next();
               if(scale == hasItem) {
                  GL11.glPushMatrix();
                  GL11.glTranslatef((float)(k + 125 + var24 * (hasItem % var27) - 2), (float)(l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)) + 14), -3.0F + super.zLevel);
                  GL11.glScalef(20.0F, 20.0F, 20.0F);
                  GL11.glTranslatef(1.0F, 0.5F, 1.0F);
                  GL11.glScalef(1.0F, 1.0F, -1.0F);
                  GL11.glRotatef(190.0F, 1.0F, 0.0F, 0.0F);
                  GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                  info = new HatInfoClient(((String)var33.getKey()).toLowerCase());
                  HatRendererHelper.renderHat(info, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, true, true, 1.0F);
                  GL11.glPopMatrix();
                  GL11.glEnable(3042);
                  GL11.glBlendFunc(770, 771);
                  GL11.glDisable(2929);
                  this.drawSolidRect(k + 125 + var24 * (hasItem % var27) + var24 - 10, l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)) + var24 - 10, 9, 9, 0, 0.4F);
                  super.fontRendererObj.drawString(HatHandler.getHatRarityColour((String)var33.getKey()).toString() + (((Integer)var33.getValue()).intValue() > 99?"99":((Integer)var33.getValue()).toString()), k + 125 + var24 * (hasItem % var27) + var24 - 5 - super.fontRendererObj.getStringWidth(((Integer)var33.getValue()).intValue() > 99?"99":((Integer)var33.getValue()).toString()) / 2, l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)) + var24 - 9, 16777215, true);
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glEnable(2929);
                  GL11.glDisable(3042);
                  break;
               }
            }
         }

         this.field_146297_k.getTextureManager().bindTexture(texIcons);
      }

      var24 = 18;
      var27 = 108 / var24;

      for(var29 = this.theirItemsForTrade.size(); var29 % 6 != 0 || var29 < 6; ++var29) {
         ;
      }

      for(hasItem = 0; hasItem < var29; ++hasItem) {
         this.drawTexturedModalRect(k + 125 + var24 * (hasItem % var27), l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)) + 36 * var30, var24 == 36?0:36, 45, var24, var24);
         if(hasItem < this.theirItemsForTrade.size()) {
            this.drawItemStack((ItemStack)this.theirItemsForTrade.get(hasItem), k + 125 + var24 * (hasItem % var27) + 1, l + 116 + var24 * (int)Math.floor((double)(hasItem / var27)) + 36 * var30 + 1);
            this.field_146297_k.getTextureManager().bindTexture(texIcons);
         }
      }

      GL11.glPopMatrix();
      RendererHelper.endGlScissor();
      this.field_146297_k.getTextureManager().bindTexture(texTradeWindow);
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      this.field_146297_k.getTextureManager().bindTexture(texIcons);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 37.0F * this.selfScrollProg, 0.0F);
      this.drawTexturedModalRect(k + 237, l + 18, this.selfCanScroll?54:66, 45, 12, 15);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 37.0F * this.theirScrollProg, 0.0F);
      this.drawTexturedModalRect(k + 237, l + 117, this.theirCanScroll?54:66, 45, 12, 15);
      GL11.glPopMatrix();
      super.drawScreen(par1, par2, par3);
      super.fontRendererObj.drawString(StatCollector.translateToLocal("hats.trade.yourOfferings"), this.guiLeft + 125, this.guiTop + 6, 2894892, false);
      super.fontRendererObj.drawString(StatCollector.translateToLocalFormatted("hats.trade.theirOfferings", new Object[]{this.trader}), this.guiLeft + 125, this.guiTop + 105, 2894892, false);
      super.fontRendererObj.drawString(this.selfReady?StatCollector.translateToLocal("hats.trade.tradeReady"):StatCollector.translateToLocal("hats.trade.tradeNotReady"), this.guiLeft + 187 - super.fontRendererObj.getStringWidth(this.selfReady?StatCollector.translateToLocal("hats.trade.tradeReady"):StatCollector.translateToLocal("hats.trade.tradeNotReady")) / 2, this.guiTop + 83, this.selfReady?8500794:7929856, false);
      super.fontRendererObj.drawString(this.theirReady?StatCollector.translateToLocal("hats.trade.tradeReady"):StatCollector.translateToLocal("hats.trade.tradeNotReady"), this.guiLeft + 187 - super.fontRendererObj.getStringWidth(this.theirReady?StatCollector.translateToLocal("hats.trade.tradeReady"):StatCollector.translateToLocal("hats.trade.tradeNotReady")) / 2, this.guiTop + 176, this.theirReady?5339428:7929856, false);
      boolean var37 = this.ourHatsForTrade.size() != 0 || this.ourItemsForTrade.size() != 0 || this.theirHatsForTrade.size() != 0 || this.theirItemsForTrade.size() != 0;
      GL11.glPushMatrix();
      float var39 = 0.5F;
      GL11.glScalef(var39, var39, var39);
      super.fontRendererObj.drawString(var37?(this.selfReady && this.theirReady?(this.pointOfNoReturn?(this.clickedMakeTrade?StatCollector.translateToLocal("hats.trade.waitingForThem"):StatCollector.translateToLocal("hats.trade.waitingForYou")):StatCollector.translateToLocal("hats.trade.bothReady")):StatCollector.translateToLocal("hats.trade.waitingForReady")):StatCollector.translateToLocal("hats.trade.waitingForOffer"), (int)((float)(this.guiLeft + 187) / var39 - (float)(super.fontRendererObj.getStringWidth(var37?(this.selfReady && this.theirReady?(this.pointOfNoReturn?(this.clickedMakeTrade?StatCollector.translateToLocal("hats.trade.waitingForThem"):StatCollector.translateToLocal("hats.trade.waitingForYou")):StatCollector.translateToLocal("hats.trade.bothReady")):StatCollector.translateToLocal("hats.trade.waitingForReady")):StatCollector.translateToLocal("hats.trade.waitingForOffer")) / 2)), (int)((float)(this.guiTop + this.ySize - 10) / var39), -16777216, false);
      GL11.glPopMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glTranslatef(0.0F, 0.0F, 75.0F);
      super.zLevel += 100.0F;
      if(this.selfReady) {
         this.drawSolidRect(this.guiLeft + 6, this.guiTop + 29, 108, 36, 0, 0.4F);
         this.drawSolidRect(this.guiLeft + 125, this.guiTop + 17, 108, 54, 0, 0.4F);
      }

      if(this.theirReady) {
         this.drawSolidRect(this.guiLeft + 125, this.guiTop + 116, 108, 54, 0, 0.4F);
      }

      super.zLevel -= 100.0F;
      GL11.glTranslatef(0.0F, 0.0F, -75.0F);
      GL11.glDisable(3042);

      for(int var34 = 0; var34 < super.buttonList.size(); ++var34) {
         GuiButton var38 = (GuiButton)super.buttonList.get(var34);
         if(var38.id == 4) {
            var38.enabled = this.selfReady && this.theirReady && !this.clickedMakeTrade;
         }
      }

      this.drawSearchBar();
      this.drawChat();
      this.drawForeground(par1, par2, par3);
   }

   public void drawItemStack(ItemStack itemstack, int par2, int par3) {
      if(itemstack != null) {
         GL11.glTranslatef(0.0F, 0.0F, 50.0F);
         if(itemstack == this.grabbedStack) {
            GL11.glTranslatef(0.0F, 0.0F, 50.0F);
         }

         GL11.glEnable('\u803a');
         RenderHelper.enableGUIStandardItemLighting();
         this.itemRenderer.renderItemAndEffectIntoGUI(this.field_146297_k.fontRenderer, this.field_146297_k.getTextureManager(), itemstack, par2, par3);
         this.itemRenderer.renderItemOverlayIntoGUI(this.field_146297_k.fontRenderer, this.field_146297_k.getTextureManager(), itemstack, par2, par3);
         RenderHelper.disableStandardItemLighting();
         GL11.glDisable('\u803a');
         if(itemstack == this.grabbedStack) {
            GL11.glTranslatef(0.0F, 0.0F, -50.0F);
         }

         GL11.glTranslatef(0.0F, 0.0F, -50.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(3042);
      }

   }

   protected void actionPerformed(GuiButton btn) {
      if(this.grabbedStack == null) {
         if(btn.id == 1) {
            this.showInv = !this.showInv;
            btn.displayString = this.showInv?StatCollector.translateToLocal("hats.trade.yourInventory"):StatCollector.translateToLocal("hats.trade.yourHats");
            this.searchBar.setText("");

            for(int hasItem = 0; hasItem < super.buttonList.size(); ++hasItem) {
               GuiButton btn1 = (GuiButton)super.buttonList.get(hasItem);
               if(btn1 instanceof GuiSlider) {
                  ((GuiSlider)btn1).sliderValue = 0.0D;
                  ((GuiSlider)btn1).updateSlider();
               }
            }
         } else if(btn.id == 3) {
            boolean var4 = this.ourHatsForTrade.size() != 0 || this.ourItemsForTrade.size() != 0 || this.theirHatsForTrade.size() != 0 || this.theirItemsForTrade.size() != 0;
            if(var4 && !this.pointOfNoReturn) {
               this.selfReady = !this.selfReady;
               PacketHandler.sendToServer(Hats.channels, new PacketPing(3, this.selfReady));
            }
         } else if(btn.id == 4) {
            this.pointOfNoReturn = true;
            this.clickedMakeTrade = true;
            PacketHandler.sendToServer(Hats.channels, new PacketPing(4, false));
         }
      }

   }

   public void onChangeSliderValue(GuiSlider slider) {
      this.sliderProg = slider.getValue();
   }

   public void drawSolidRect(int par0, int par1, int par2, int par3, int par4, float alpha) {
      float f1 = (float)(par4 >> 16 & 255) / 255.0F;
      float f2 = (float)(par4 >> 8 & 255) / 255.0F;
      float f3 = (float)(par4 & 255) / 255.0F;
      Tessellator tessellator = Tessellator.instance;
      GL11.glDisable(3553);
      GL11.glColor4f(f1, f2, f3, alpha);
      tessellator.startDrawingQuads();
      tessellator.addVertex((double)(par0 + 0), (double)(par1 + par3), (double)super.zLevel);
      tessellator.addVertex((double)(par0 + par2), (double)(par1 + par3), (double)super.zLevel);
      tessellator.addVertex((double)(par0 + par2), (double)(par1 + 0), (double)super.zLevel);
      tessellator.addVertex((double)(par0 + 0), (double)(par1 + 0), (double)super.zLevel);
      tessellator.draw();
      GL11.glEnable(3553);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawSearchBar() {
      if(this.searchBar.getVisible()) {
         this.searchBar.drawTextBox();
      }

   }

   public void drawChat() {
      if(this.chatBar.getVisible()) {
         this.chatBar.drawTextBox();
      }

      RendererHelper.startGlScissor(this.guiLeft + 6, this.guiTop + 113, 101, 97);
      GL11.glPushMatrix();
      float scale = 0.5F;
      GL11.glScalef(scale, scale, scale);
      int lines = 0;

      int i;
      String msg;
      List list;
      for(i = 0; i < this.chatMessages.size(); ++i) {
         msg = (String)this.chatMessages.get(i);
         list = super.fontRendererObj.listFormattedStringToWidth(msg, 196);
         lines += list.size();
      }

      if(lines > 19) {
         GL11.glTranslatef(0.0F, (float)(-(lines - 19)) * 10.0F * (1.0F - this.chatScroll), 0.0F);
      }

      lines = 0;

      for(i = 0; i < this.chatMessages.size(); ++i) {
         msg = (String)this.chatMessages.get(i);
         list = super.fontRendererObj.listFormattedStringToWidth(msg, 196);

         for(int kk = 0; kk < list.size(); ++kk) {
            String[] split = msg.split(" ");
            if(msg.startsWith(StatCollector.translateToLocal("hats.trade.terminatePrefix"))) {
               if(kk == 0) {
                  super.fontRendererObj.drawString(" " + (String)list.get(kk), (int)((float)(this.guiLeft + 8) / scale), (int)((float)(this.guiTop + 115 + lines * 5) / scale), 7929856, false);
               } else {
                  super.fontRendererObj.drawString(" " + (String)list.get(kk), (int)((float)(this.guiLeft + 8) / scale), (int)((float)(this.guiTop + 115 + lines * 5) / scale), 7929856, false);
               }
            } else if(kk == 0) {
               String line = (String)list.get(kk);
               String prefix = "";
               if(line.startsWith(this.field_146297_k.thePlayer.getCommandSenderName()) && line.substring(this.field_146297_k.thePlayer.getCommandSenderName().length()).startsWith(":")) {
                  prefix = this.field_146297_k.thePlayer.getCommandSenderName() + ":";
               }

               if(line.startsWith(this.trader) && line.substring(this.trader.length()).startsWith(":")) {
                  prefix = this.trader + ":";
               }

               super.fontRendererObj.drawString(prefix, (int)((float)(this.guiLeft + 8) / scale), (int)((float)(this.guiTop + 115 + lines * 5) / scale), 0, false);
               super.fontRendererObj.drawString(line.substring(prefix.length()), (int)((float)(this.guiLeft + 8) / scale + (float)super.fontRendererObj.getStringWidth(prefix)), (int)((float)(this.guiTop + 115 + lines * 5) / scale), 14737632, false);
            } else {
               super.fontRendererObj.drawString(" " + (String)list.get(kk), (int)((float)(this.guiLeft + 8) / scale), (int)((float)(this.guiTop + 115 + lines * 5) / scale), 14737632, false);
            }

            ++lines;
         }
      }

      RendererHelper.endGlScissor();
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if(lines > 19) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -82.0F * this.chatScroll, 0.0F);
         this.field_146297_k.getTextureManager().bindTexture(texIcons);
         this.drawTexturedModalRect(this.guiLeft + 107, this.guiTop + 195, 78, 45, 4, 15);
         GL11.glPopMatrix();
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

}
