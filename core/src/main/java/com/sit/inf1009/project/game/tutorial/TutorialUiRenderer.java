package com.sit.inf1009.project.game.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.sit.inf1009.project.game.domain.FoodCategory;

import java.util.Map;

public class TutorialUiRenderer {

    private static final float BASE_FONT_SCALE = 0.95f;
    private static final float MIN_FONT_SCALE = 0.72f;
    private static final float SCALE_STEP = 0.05f;

    private static final float PANEL_MARGIN = 18f;
    private static final float PANEL_PADDING = 16f;
    private static final float PANEL_WIDTH = 430f;
    private static final float PANEL_HEIGHT = 285f;

    private static final float TEXT_GAP = 8f;
    private static final float FOOD_LINE_GAP = 6f;
    private static final float ICON_BASE_SIZE = 18f;
    private static final float ICON_GAP = 8f;

    public void renderTutorialInstructionsPanel(
            ShapeRenderer shapeRenderer,
            SpriteBatch batch,
            BitmapFont font,
            Map<FoodCategory, Texture> foodCategoryTextures,
            TutorialState tutorialState) {

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float panelW = PANEL_WIDTH;
        float panelH = PANEL_HEIGHT;

        if (screenW < panelW + (PANEL_MARGIN * 2f)) {
            panelW = screenW - (PANEL_MARGIN * 2f);
        }
        if (screenH < panelH + (PANEL_MARGIN * 2f)) {
            panelH = screenH - (PANEL_MARGIN * 2f);
        }

        panelW = Math.max(220f, panelW);
        panelH = Math.max(180f, panelH);

        float panelX = PANEL_MARGIN;
        float panelY = PANEL_MARGIN;

        float contentWidth = panelW - (PANEL_PADDING * 2f);
        float topY = panelY + panelH - 18f;
        float bottomLimit = panelY + PANEL_PADDING;
        float availableHeight = topY - bottomLimit;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.65f);
        shapeRenderer.rect(panelX, panelY, panelW, panelH);
        shapeRenderer.end();

        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;

        float fittedScale = resolveFittedScale(font, contentWidth, availableHeight, tutorialState);

        batch.begin();

        font.getData().setScale(fittedScale);
        font.setColor(1f, 1f, 1f, 0.78f);

        GlyphLayout layout = new GlyphLayout();

        float textX = panelX + PANEL_PADDING;
        float y = topY;

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit, "Tutorial:");
        y -= TEXT_GAP;

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit, "Welcome to the game!");
        y -= TEXT_GAP;

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit, "How to eat healthily:");
        y -= TEXT_GAP;

        y = drawTutorialFoodLine(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "2-4 Vegetables", foodCategoryTextures.get(FoodCategory.VEGETABLE), fittedScale);
        y = drawTutorialFoodLine(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "1-3 Protein", foodCategoryTextures.get(FoodCategory.PROTEIN), fittedScale);
        y = drawTutorialFoodLine(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "1-2 Carbohydrates", foodCategoryTextures.get(FoodCategory.CARBOHYDRATE), fittedScale);
        y = drawTutorialFoodLine(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "0-1 Oil", foodCategoryTextures.get(FoodCategory.OIL), fittedScale);

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "Move with the WASD keys, and submit your plate with the Enter key");
        y -= TEXT_GAP;

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "Press escape to pause the game");
        y -= TEXT_GAP;

        y = drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "Collect 3 correct plates to pass the tutorial!");
        y -= TEXT_GAP;

        drawWrappedText(batch, font, layout, textX, y, contentWidth, bottomLimit,
                "Progress: " + tutorialState.getCorrectPlateCount() + "/3");

        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(oldScaleX, oldScaleY);

        batch.end();
    }

    private float drawTutorialFoodLine(
            SpriteBatch batch,
            BitmapFont font,
            GlyphLayout layout,
            float textX,
            float y,
            float contentWidth,
            float bottomLimit,
            String label,
            Texture icon,
            float fontScale) {

        float iconSize = getIconSizeForScale(fontScale);
        float textWidth = contentWidth;

        if (icon != null) {
            textWidth = contentWidth - iconSize - ICON_GAP;
        }

        layout.setText(font, label, font.getColor(), textWidth, Align.left, true);

        float lineHeight = layout.height;
        if (icon != null) {
            lineHeight = Math.max(lineHeight, iconSize);
        }

        if (y - lineHeight < bottomLimit) {
            return bottomLimit;
        }

        font.draw(batch, layout, textX, y);

        if (icon != null) {
            float iconX = textX + textWidth + ICON_GAP;
            float iconY = y - lineHeight + ((lineHeight - iconSize) / 2f);
            batch.draw(icon, iconX, iconY, iconSize, iconSize);
        }

        return y - lineHeight - FOOD_LINE_GAP;
    }

    private float drawWrappedText(
            SpriteBatch batch,
            BitmapFont font,
            GlyphLayout layout,
            float x,
            float y,
            float width,
            float bottomLimit,
            String text) {

        layout.setText(font, text, font.getColor(), width, Align.left, true);

        if (y - layout.height < bottomLimit) {
            return bottomLimit;
        }

        font.draw(batch, layout, x, y);
        return y - layout.height;
    }

    private float resolveFittedScale(
            BitmapFont font,
            float contentWidth,
            float availableHeight,
            TutorialState tutorialState) {

        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;

        float fittedScale = MIN_FONT_SCALE;

        for (float scale = BASE_FONT_SCALE; scale >= MIN_FONT_SCALE; scale -= SCALE_STEP) {
            font.getData().setScale(scale);

            float totalHeight = measureContentHeight(font, contentWidth, tutorialState, scale);
            if (totalHeight <= availableHeight) {
                fittedScale = scale;
                break;
            }
        }

        font.getData().setScale(oldScaleX, oldScaleY);
        return fittedScale;
    }

    private float measureContentHeight(
            BitmapFont font,
            float contentWidth,
            TutorialState tutorialState,
            float fontScale) {

        GlyphLayout layout = new GlyphLayout();
        float totalHeight = 0f;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth, "Tutorial:");
        totalHeight += TEXT_GAP;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth, "Welcome to the game!");
        totalHeight += TEXT_GAP;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth, "How to eat healthily:");
        totalHeight += TEXT_GAP;

        totalHeight += measureFoodLineHeight(font, layout, contentWidth, "2-4 Vegetables", fontScale);
        totalHeight += measureFoodLineHeight(font, layout, contentWidth, "1-3 Protein", fontScale);
        totalHeight += measureFoodLineHeight(font, layout, contentWidth, "1-2 Carbohydrates", fontScale);
        totalHeight += measureFoodLineHeight(font, layout, contentWidth, "0-1 Oil", fontScale);

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth,
                "Move with the WASD keys, and submit your plate with the Enter key");
        totalHeight += TEXT_GAP;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth,
                "Press escape to pause the game");
        totalHeight += TEXT_GAP;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth,
                "Collect 3 correct plates to pass the tutorial!");
        totalHeight += TEXT_GAP;

        totalHeight += measureWrappedTextHeight(font, layout, contentWidth,
                "Progress: " + tutorialState.getCorrectPlateCount() + "/3");

        return totalHeight;
    }

    private float measureWrappedTextHeight(
            BitmapFont font,
            GlyphLayout layout,
            float width,
            String text) {

        layout.setText(font, text, font.getColor(), width, Align.left, true);
        return layout.height;
    }

    private float measureFoodLineHeight(
            BitmapFont font,
            GlyphLayout layout,
            float contentWidth,
            String label,
            float fontScale) {

        float iconSize = getIconSizeForScale(fontScale);
        float textWidth = contentWidth - iconSize - ICON_GAP;

        layout.setText(font, label, font.getColor(), textWidth, Align.left, true);

        return Math.max(layout.height, iconSize) + FOOD_LINE_GAP;
    }

    private float getIconSizeForScale(float fontScale) {
        return ICON_BASE_SIZE * (fontScale / BASE_FONT_SCALE);
    }

    public Rectangle createContinueButton() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        return new Rectangle(
                screenW / 2f - 160f,
                screenH / 2f - 70f,
                320f,
                48f
        );
    }

    public void renderTutorialCompleteOverlay(
            ShapeRenderer shapeRenderer,
            SpriteBatch batch,
            BitmapFont font,
            Rectangle continueButton) {

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 1f);
        shapeRenderer.rect(0f, 0f, screenW, screenH);
        shapeRenderer.rect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
        shapeRenderer.end();

        batch.begin();
        font.setColor(1f, 1f, 1f, 1f);

        font.draw(batch, "Now you know how to play,", screenW / 2f - 110f, screenH / 2f + 55f);
        font.draw(batch, "why not give it a real go?", screenW / 2f - 110f, screenH / 2f + 25f);
        font.draw(batch, "Go to Player Selection", continueButton.x + 52f, continueButton.y + 30f);

        batch.end();
    }
}