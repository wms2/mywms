/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;

/**
 *
 * @author artur
 * 
 */


public class FocusTraversalPolicyInitial  extends DefaultFocusTraversalPolicy
    {
        FocusTraversalPolicy policy;
        Component initialComponent;

    @Override
    public Component getDefaultComponent(Container aContainer) {
                    return initialComponent;
//        return super.getDefaultComponent(aContainer);
    }

        public FocusTraversalPolicyInitial(FocusTraversalPolicy policy, Component initialComponent) {
            
            this.policy = policy;
            this.initialComponent = initialComponent;
        }

        @Override
        public Component getInitialComponent(Window window) {
            return initialComponent;
//            return policy.getInitialComponent(window);

        }
        
        
        //alternate method = addButton.setNextFocusableComponent(printnormComboBox);        
        public Component getComponentAfter(Container focusCycleRoot,
                                           Component aComponent)
        {
/*            if (aComponent == addButton) {
                return printnormComboBox;
            }
            return null;*/
            return policy.getComponentAfter(focusCycleRoot, aComponent);
        }

        public Component getComponentBefore(Container focusCycleRoot,
                                            Component aComponent) {
            return policy.getComponentBefore(focusCycleRoot, aComponent);
        }
        

    }
