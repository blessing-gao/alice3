/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.lgna.croquet;

import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.javax.swing.models.AbstractMutableTreeModel;
import edu.cmu.cs.dennisc.javax.swing.models.TreeModel;

import javax.swing.tree.TreePath;
import java.util.List;
import java.util.UUID;

/**
 * @author Dennis Cosgrove
 */
public abstract class CustomSingleSelectTreeState<T> extends SingleSelectTreeState<T> {
  private final AbstractMutableTreeModel<T> treeModel = new AbstractMutableTreeModel<T>() {
    @Override
    public int getChildCount(Object parent) {
      return CustomSingleSelectTreeState.this.getChildCount((T) parent);
    }

    @Override
    public T getChild(Object parent, int index) {
      return CustomSingleSelectTreeState.this.getChild((T) parent, index);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
      return CustomSingleSelectTreeState.this.getIndexOfChild((T) parent, (T) child);
    }

    @Override
    public T getRoot() {
      return CustomSingleSelectTreeState.this.getRoot();
    }

    @Override
    public boolean isLeaf(Object node) {
      return CustomSingleSelectTreeState.this.isLeaf((T) node);
    }

    private Object[] getPathToRoot(T node) {
      List<T> collection = Lists.newLinkedList();
      T n = node;
      if (n != null) {
        T root = this.getRoot();
        while (n != root) {
          T parent = CustomSingleSelectTreeState.this.getParent(n);
          if (parent != null) {
            collection.add(0, n);
            n = parent;
          } else {
            break;
          }
        }
        collection.add(0, root);
      }
      return collection.toArray();
    }

    @Override
    public TreePath getTreePath(Object node) {
      if (node != null) {
        Object[] nodes = this.getPathToRoot((T) node);
        assert nodes != null : CustomSingleSelectTreeState.this;
        assert nodes.length > 0 : CustomSingleSelectTreeState.this;
        return new TreePath(nodes);
      } else {
        return null;
      }
    }
  };

  public CustomSingleSelectTreeState(Group group, UUID id, T initialSelection, ItemCodec<T> itemCodec) {
    super(group, id, initialSelection, itemCodec);
  }

  protected abstract int getChildCount(T parent);

  protected abstract T getChild(T parent, int index);

  protected abstract int getIndexOfChild(T parent, T child);

  protected abstract T getRoot();

  @Override
  public abstract boolean isLeaf(T node);

  @Override
  public TreeModel<T> getTreeModel() {
    return this.treeModel;
  }

  @Override
  public void refresh(T node) {
    this.treeModel.reload(node);
  }
}
