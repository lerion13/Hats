package hats.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hats.common.Hats;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTradeReq extends Gui {

   private static final ResourceLocation texAchi = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   private Minecraft theGame;
   private int width;
   private int height;
   private String headerText;
   private String hatNameText;
   private long unlockedTime;
   public ArrayList hatList = new ArrayList();


   public GuiTradeReq(Minecraft par1Minecraft) {
      this.theGame = par1Minecraft;
      this.headerText = "§e" + StatCollector.translateToLocal("hats.trade.newTradeRequest");
      this.hatNameText = "";
   }

   public void queueHatUnlocked(String hat) {
      if(!this.hatList.contains(hat)) {
         this.hatList.add(hat);
      }

      this.showNextHatUnlocked();
   }

   public void showNextHatUnlocked() {
      if(this.hatList.size() > 0 && this.unlockedTime == 0L) {
         this.hatNameText = (String)this.hatList.get(0);
         this.unlockedTime = Minecraft.getSystemTime();
         this.hatList.remove(0);
      }

   }

   private void updateWindowScale() {
      GL11.glViewport(0, 0, this.theGame.displayWidth, this.theGame.displayHeight);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      this.width = this.theGame.displayWidth;
      this.height = this.theGame.displayHeight;
      ScaledResolution scaledresolution = new ScaledResolution(this.theGame, this.theGame.displayWidth, this.theGame.displayHeight);
      this.width = scaledresolution.getScaledWidth();
      this.height = scaledresolution.getScaledHeight();
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
   }

   public void updateGui() {
      GL11.glPushMatrix();
      if(this.unlockedTime != 0L) {
         double d0 = (double)(Minecraft.getSystemTime() - this.unlockedTime) / 10000.0D;
         if(d0 >= 0.0D && d0 <= 1.0D && !this.hatNameText.equalsIgnoreCase("")) {
            this.updateWindowScale();
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            double d1 = d0 * 2.0D;
            if(d1 > 1.0D) {
               d1 = 2.0D - d1;
            }

            d1 *= 4.0D;
            d1 = 1.0D - d1;
            if(d1 < 0.0D) {
               d1 = 0.0D;
            }

            d1 *= d1;
            d1 *= d1;
            int i = this.width - 160;
            int j = 0 - (int)(d1 * 36.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            this.theGame.getTextureManager().bindTexture(texAchi);
            GL11.glDisable(2896);
            this.drawTexturedModalRect(i, j, 96, 202, 160, 32);
            this.theGame.fontRenderer.drawString(this.headerText, i + 30, j + 7, -256);
            FontRenderer var10000 = this.theGame.fontRenderer;
            Object[] var10002 = new Object[1];
            GameSettings var10005 = this.theGame.gameSettings;
            var10002[0] = GameSettings.getKeyDisplayString(Hats.config.getKeyBind("guiKeyBind").keyIndex);
            var10000.drawString(StatCollector.translateToLocalFormatted("hats.trade.tradeRequestDesc", var10002), i + 30, j + 18, -1);
            ResourceLocation rl = null;

            for(int xOff = 0; xOff < this.theGame.theWorld.playerEntities.size(); ++xOff) {
               AbstractClientPlayer player = (AbstractClientPlayer)this.theGame.theWorld.playerEntities.get(xOff);
               if(player.getCommandSenderName().equalsIgnoreCase(this.hatNameText)) {
                  rl = player.getLocationSkin();
               }
            }

            if(rl != null) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.theGame.getTextureManager().bindTexture(rl);
               double var15 = 6.0D;
               double yOff = 6.0D;
               double size = 20.0D;
               Tessellator tessellator = Tessellator.instance;
               tessellator.startDrawingQuads();
               tessellator.addVertexWithUV((double)i + var15 + 0.0D, (double)j + yOff + size, (double)super.zLevel, 0.125D, 0.5D);
               tessellator.addVertexWithUV((double)i + var15 + size, (double)j + yOff + size, (double)super.zLevel, 0.25D, 0.5D);
               tessellator.addVertexWithUV((double)i + var15 + size, (double)j + yOff + 0.0D, (double)super.zLevel, 0.25D, 0.25D);
               tessellator.addVertexWithUV((double)i + var15 + 0.0D, (double)j + yOff + 0.0D, (double)super.zLevel, 0.125D, 0.25D);
               tessellator.draw();
            }

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(2896);
         } else {
            this.unlockedTime = 0L;
            this.showNextHatUnlocked();
         }
      }

      GL11.glPopMatrix();
   }

}
