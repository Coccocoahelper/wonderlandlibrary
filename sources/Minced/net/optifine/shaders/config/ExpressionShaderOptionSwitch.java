// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.shaders.config;

import net.optifine.expr.IExpressionBool;

public class ExpressionShaderOptionSwitch implements IExpressionBool
{
    private ShaderOptionSwitch shaderOption;
    
    public ExpressionShaderOptionSwitch(final ShaderOptionSwitch shaderOption) {
        this.shaderOption = shaderOption;
    }
    
    @Override
    public boolean eval() {
        return ShaderOptionSwitch.isTrue(this.shaderOption.getValue());
    }
    
    @Override
    public String toString() {
        return "" + this.shaderOption;
    }
}
