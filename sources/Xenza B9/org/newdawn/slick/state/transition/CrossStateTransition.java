// 
// Decompiled by Procyon v0.6.0
// 

package org.newdawn.slick.state.transition;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.GameState;

public abstract class CrossStateTransition implements Transition
{
    private GameState secondState;
    
    public CrossStateTransition(final GameState secondState) {
        this.secondState = secondState;
    }
    
    public abstract boolean isComplete();
    
    public void postRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
        this.preRenderSecondState(game, container, g);
        this.secondState.render(container, game, g);
        this.postRenderSecondState(game, container, g);
    }
    
    public void preRender(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
        this.preRenderFirstState(game, container, g);
    }
    
    public void update(final StateBasedGame game, final GameContainer container, final int delta) throws SlickException {
    }
    
    public void preRenderFirstState(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
    }
    
    public void preRenderSecondState(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
    }
    
    public void postRenderSecondState(final StateBasedGame game, final GameContainer container, final Graphics g) throws SlickException {
    }
}
