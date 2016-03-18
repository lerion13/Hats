package hats.client.gui;

import hats.common.Hats;
import hats.common.packet.PacketString;
import ichun.common.core.network.PacketHandler;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiTradeMaker extends GuiScreen {

   public static final ResourceLocation texMaker = new ResourceLocation("hats", "textures/gui/trademaker.png");
   protected int xSize = 176;
   protected int ySize = 170;
   protected int guiLeft;
   protected int guiTop;
   public boolean forced;
   public final int ID_CANCEL = 100;
   public ArrayList players = new ArrayList();


   public void updateScreen() {
      if(super.mc.theWorld.getWorldTime() % 10L == 3L || this.forced) {
         this.forced = false;
         this.players.clear();

         int i;
         for(i = 0; i < super.mc.theWorld.playerEntities.size(); ++i) {
            EntityPlayer player = (EntityPlayer)super.mc.theWorld.playerEntities.get(i);
            if(player != super.mc.thePlayer && player.isEntityAlive() && !this.players.contains(player.getCommandSenderName()) && (double)player.getDistanceToEntity(super.mc.thePlayer) < 16.0D && player.canEntityBeSeen(super.mc.thePlayer)) {
               this.players.add(player.getCommandSenderName());
            }
         }

         Collections.sort(this.players);
         super.buttonList.clear();

         for(i = 0; i < this.players.size() && i != 12; ++i) {
            super.buttonList.add(new GuiButton(i, this.guiLeft + 6 + (i % 2 == 1?84:0), this.guiTop + 4 + 22 * (int)Math.floor((double)i / 2.0D), 80, 20, (String)this.players.get(i)));
         }

         super.buttonList.add(new GuiButton(100, super.width / 2 - 45, this.guiTop + this.ySize - 31, 90, 20, StatCollector.translateToLocal("gui.cancel")));
      }

   }

   public void initGui() {
      this.guiLeft = (super.width - this.xSize) / 2;
      this.guiTop = (super.height - this.ySize) / 2;
      this.forced = true;
   }

   protected void actionPerformed(GuiButton btn) {
      if(btn.id != 100) {
         PacketHandler.sendToServer(Hats.channels, new PacketString(0, (String)this.players.get(btn.id)));
      }

      super.mc.displayGuiScreen((GuiScreen)null);
      super.mc.setIngameFocus();
   }

   public void drawScreen(int par1, int par2, float par3) {
      if(super.mc == null) {
         super.mc = Minecraft.getMinecraft();
         super.fontRendererObj = super.mc.fontRenderer;
      }

      this.drawDefaultBackground();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.getTextureManager().bindTexture(texMaker);
      int k = this.guiLeft;
      int l = this.guiTop;
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      this.drawString(super.fontRendererObj, StatCollector.translateToLocal("hats.trade.selectTrader"), this.guiLeft + 1, this.guiTop - 9, 16777215);
      super.drawScreen(par1, par2, par3);
   }

}
