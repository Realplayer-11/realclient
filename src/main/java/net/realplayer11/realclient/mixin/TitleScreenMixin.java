package net.realplayer11.realclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {

    // Colori Real Client (viola / nero / bianco)
    private static final int COLOR_BG_DARK      = 0xFF0A0A0F;
    private static final int COLOR_PURPLE_DARK   = 0xFF3A0870;
    private static final int COLOR_PURPLE_MID    = 0xFF7020C0;
    private static final int COLOR_PURPLE_LIGHT  = 0xFFC080FF;
    private static final int COLOR_WHITE         = 0xFFFFFFFF;
    private static final int COLOR_GRAY_MUTED    = 0xFF6030A0;
    private static final int COLOR_PANEL_BG      = 0xCC19082D;
    private static final int COLOR_PANEL_BORDER  = 0xFF4A1490;

    // Titolo schermata — non usato direttamente ma richiesto dal costruttore super
    protected TitleScreenMixin() {
        super(Text.literal("Real Client"));
    }

    /**
     * Sostituisce il rendering dello sfondo della schermata principale.
     * Disegniamo sfondo nero + gradiente viola + griglia + titolo.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void realClient_render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;

        // --- SFONDO NERO ---
        context.fill(0, 0, w, h, COLOR_BG_DARK);

        // --- GRADIENTE VIOLA CENTRALE (simulato con rettangoli semitrasparenti) ---
        int gradW = w / 2;
        int gradH = h / 2;
        int cx = w / 2;
        int cy = (int)(h * 0.65f);
        context.fill(cx - gradW, cy - gradH, cx + gradW, cy + gradH, 0x1A6400B4);
        context.fill(cx - gradW/2, cy - gradH/2, cx + gradW/2, cy + gradH/2, 0x1A9020E0);

        // --- GRIGLIA (linee orizzontali e verticali viola) ---
        int gridStep = 40;
        int gridColor = 0x12780FC8;
        for (int gx = 0; gx < w; gx += gridStep) {
            context.fill(gx, 0, gx + 1, h, gridColor);
        }
        for (int gy = 0; gy < h; gy += gridStep) {
            context.fill(0, gy, w, gy + 1, gridColor);
        }

        // --- TOPBAR ---
        context.fill(0, 0, w, 32, 0xCC0A0314);
        context.fill(0, 31, w, 32, COLOR_PANEL_BORDER);

        // Nome utente in topbar
        String username = "REAL_PLAYER_11";
        int userBadgeX = w - 160;
        context.fill(userBadgeX, 6, userBadgeX + 150, 26, 0x33701EC8);
        context.fill(userBadgeX, 6, userBadgeX + 151, 7, COLOR_PANEL_BORDER);
        context.fill(userBadgeX, 25, userBadgeX + 151, 26, COLOR_PANEL_BORDER);
        context.drawText(this.textRenderer, Text.literal(username),
                userBadgeX + 8, 12, COLOR_PURPLE_LIGHT, false);

        // --- LOGO / TITOLO CENTRALE ---
        int logoY = h / 2 - 120;

        // Cerchio logo
        int logoR = 36;
        int logoCX = w / 2;
        int logoCY = logoY + logoR;
        // Simula cerchio con un rettangolo arrotondato (fill quadrato + testo)
        context.fill(logoCX - logoR, logoCY - logoR, logoCX + logoR, logoCY + logoR, COLOR_PURPLE_DARK);
        context.fill(logoCX - logoR + 2, logoCY - logoR, logoCX + logoR - 2, logoCY + logoR, 0xFF5A1090);
        // "R" nel logo
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("R"),
                logoCX, logoCY - 4, COLOR_WHITE);

        // Testo "Real Client"
        int titleY = logoCY + logoR + 12;
        // "REAL" in bianco grassetto (grande)
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§lREAL §r§5CLIENT"),
                w / 2, titleY, COLOR_WHITE);

        // Sottotitolo versione
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§8Real Client 1.0.0 — by Real_Player_11"),
                w / 2, titleY + 14, 0xFF444444);

        // --- PANNELLO MENU CENTRALE ---
        int panelW = 260;
        int panelX = w / 2 - panelW / 2;
        int panelY = titleY + 36;
        int btnH = 28;
        int btnGap = 6;
        int numButtons = 5; // Singolo, Multi, Server, Cosmetici, Negozio
        int panelH = numButtons * (btnH + btnGap) + btnGap + 10 + btnH + btnGap;

        // Sfondo pannello
        context.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, COLOR_PANEL_BORDER);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, COLOR_PANEL_BG);

        // Non ridisegniamo i bottoni qui (li gestiamo in init) — solo il pannello sfondo

        // --- VERSIONE IN BASSO ---
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§8Real Client 1.0.0 (release/real_alpha)"),
                w / 2, h - 14, 0xFF333333);

        // NON cancelliamo il callback — lasciamo che Minecraft aggiunga i widget normali
        // (i bottoni verranno posizionati da init)
    }

    /**
     * Riposiziona e recolora i bottoni standard di Minecraft
     * per adattarli al layout del Real Client.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void realClient_init(CallbackInfo ci) {
        int w = this.width;
        int h = this.height;

        // Rimuoviamo tutti i widget esistenti e li riaggiungiamo personalizzati
        this.clearChildren();

        int panelW = 260;
        int panelX = w / 2 - panelW / 2;
        int btnH = 26;
        int btnGap = 6;

        // Calcola Y di inizio bottoni (sotto il titolo)
        int startY = h / 2 - 20;

        // Bottone: Giocatore Singolo
        this.addDrawableChild(RealButton.of(
                Text.literal("  \u25B6  Giocatore singolo"),
                panelX, startY,
                panelW, btnH,
                btn -> this.client.setScreen(new net.minecraft.client.gui.screen.world.SelectWorldScreen((TitleScreen)(Object)this))
        ));

        // Bottone: Multigiocatore
        this.addDrawableChild(RealButton.of(
                Text.literal("  \u25B6  Multigiocatore"),
                panelX, startY + (btnH + btnGap),
                panelW, btnH,
                btn -> this.client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen((TitleScreen)(Object)this))
        ));

        // Bottone: Opzioni (al posto di Server Partner)
        this.addDrawableChild(RealButton.of(
                Text.literal("  \u2605  Opzioni"),
                panelX, startY + 2 * (btnH + btnGap),
                panelW, btnH,
                btn -> this.client.setScreen(new net.minecraft.client.gui.screen.option.OptionsScreen((TitleScreen)(Object)this, this.client.options))
        ));

        // Bottone: Skin / Cosmetici (apre la schermata profilo)
        this.addDrawableChild(RealButton.of(
                Text.literal("  \u25A0  Cosmetici"),
                panelX, startY + 3 * (btnH + btnGap),
                panelW, btnH,
                btn -> {} // placeholder
        ));

        // Bottone NEGOZIO (viola pieno)
        this.addDrawableChild(RealButtonHighlight.of(
                Text.literal("\uD83D\uDED2  NEGOZIO"),
                panelX, startY + 4 * (btnH + btnGap) + 4,
                panelW, btnH,
                btn -> {} // placeholder
        ));

        // Bottone Esci dal gioco
        this.addDrawableChild(RealButton.ofQuit(
                Text.literal("Esci dal gioco"),
                panelX, startY + 5 * (btnH + btnGap) + 8,
                panelW, btnH - 2,
                btn -> this.client.scheduleStop()
        ));
    }

    // -------------------------------------------------------------------------
    // Classi interne per bottoni personalizzati
    // -------------------------------------------------------------------------

    static class RealButton extends ButtonWidget {
        private static final int BG     = 0xCC19082D;
        private static final int BORDER = 0xFF4A1490;
        private static final int HOVER  = 0xCC501499;
        private static final int TEXT   = 0xFFD0B0F5;

        protected RealButton(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        public static RealButton of(Text msg, int x, int y, int w, int h, PressAction action) {
            return new RealButton(x, y, w, h, msg, action);
        }

        public static RealButton ofQuit(Text msg, int x, int y, int w, int h, PressAction action) {
            return new RealButton(x, y, w, h, msg, action) {
                @Override
                public void renderWidget(DrawContext ctx, int mx, int my, float delta) {
                    int bg = this.isHovered() ? 0xCC3C1060 : 0xAA0A0314;
                    ctx.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
                    ctx.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, 0xFF2A0A50);
                    ctx.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, 0xFF2A0A50);
                    ctx.drawCenteredTextWithShadow(
                            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                            this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFF604090);
                }
            };
        }

        @Override
        public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
            int bg = this.isHovered() ? HOVER : BG;
            ctx.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
            // Bordo
            ctx.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, BORDER);
            ctx.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, BORDER);
            ctx.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, BORDER);
            ctx.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, BORDER);

            ctx.drawTextWithShadow(
                    net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    this.getMessage(),
                    this.getX() + 10, this.getY() + (this.height - 8) / 2,
                    this.isHovered() ? 0xFFF0D8FF : TEXT);
        }
    }

    static class RealButtonHighlight extends ButtonWidget {
        protected RealButtonHighlight(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        public static RealButtonHighlight of(Text msg, int x, int y, int w, int h, PressAction action) {
            return new RealButtonHighlight(x, y, w, h, msg, action);
        }

        @Override
        public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
            int bg = this.isHovered() ? 0xFF8030D0 : 0xFF7020C0;
            ctx.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bg);
            ctx.drawCenteredTextWithShadow(
                    net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    this.getMessage(),
                    this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2,
                    0xFFFFFFFF);
        }
    }
}
