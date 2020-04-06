package org.sirix.access.trx.node.xml;

import org.sirix.api.PageTrx;
import org.sirix.axis.LevelOrderAxis;
import org.sirix.exception.SirixException;
import org.sirix.node.SirixDeweyID;
import org.sirix.node.interfaces.DataRecord;
import org.sirix.node.interfaces.Node;
import org.sirix.node.interfaces.StructNode;
import org.sirix.page.PageKind;
import org.sirix.page.UnorderedKeyValuePage;

public class DeweyIDManager {
  private final XmlNodeTrxImpl nodeTrx;

  private final PageTrx<Long, DataRecord, UnorderedKeyValuePage> pageTrx;

  public DeweyIDManager(XmlNodeTrxImpl nodeTrx) {
    this.nodeTrx = nodeTrx;
    this.pageTrx = nodeTrx.getPageWtx();
  }

  /**
   * Compute the new DeweyIDs.
   *
   * @throws SirixException if anything went wrong
   */
  public void computeNewDeweyIDs() {
    SirixDeweyID id;
    if (nodeTrx.hasLeftSibling() && nodeTrx.hasRightSibling()) {
      id = SirixDeweyID.newBetween(nodeTrx.getLeftSiblingDeweyID(), nodeTrx.getRightSiblingDeweyID());
    } else if (nodeTrx.hasLeftSibling()) {
      id = SirixDeweyID.newBetween(nodeTrx.getLeftSiblingDeweyID(), null);
    } else if (nodeTrx.hasRightSibling()) {
      id = SirixDeweyID.newBetween(null, nodeTrx.getRightSiblingDeweyID());
    } else {
      id = nodeTrx.getParentDeweyID().getNewChildID();
    }

    final long nodeKey = nodeTrx.getNodeKey();

    final StructNode root = (StructNode) nodeTrx.getPageWtx().prepareEntryForModification(nodeKey, PageKind.RECORDPAGE, -1);
    root.setDeweyID(id);

    if (root.hasFirstChild()) {
      final Node firstChild =
          (Node) pageTrx.prepareEntryForModification(root.getFirstChildKey(), PageKind.RECORDPAGE, -1);
      firstChild.setDeweyID(id.getNewChildID());

      int previousLevel = nodeTrx.getDeweyID().getLevel();
      nodeTrx.moveTo(firstChild.getNodeKey());
      int attributeNr = 0;
      int nspNr = 0;
      for (@SuppressWarnings("unused")
      final long key : LevelOrderAxis.newBuilder(nodeTrx).includeNonStructuralNodes().build()) {
        SirixDeweyID deweyID;
        if (nodeTrx.isAttribute()) {
          final long attNodeKey = nodeTrx.getNodeKey();
          if (attributeNr == 0) {
            deweyID = nodeTrx.getParentDeweyID().getNewAttributeID();
          } else {
            nodeTrx.moveTo(attributeNr - 1);
            deweyID = SirixDeweyID.newBetween(nodeTrx.getDeweyID(), null);
          }
          nodeTrx.moveTo(attNodeKey);
          attributeNr++;
        } else if (nodeTrx.isNamespace()) {
          final long nspNodeKey = nodeTrx.getNodeKey();
          if (nspNr == 0) {
            deweyID = nodeTrx.getParentDeweyID().getNewNamespaceID();
          } else {
            nodeTrx.moveTo(nspNr - 1);
            deweyID = SirixDeweyID.newBetween(nodeTrx.getDeweyID(), null);
          }
          nodeTrx.moveTo(nspNodeKey);
          nspNr++;
        } else {
          attributeNr = 0;
          nspNr = 0;
          if (previousLevel + 1 == nodeTrx.getDeweyID().getLevel()) {
            if (nodeTrx.hasLeftSibling()) {
              deweyID = SirixDeweyID.newBetween(nodeTrx.getLeftSiblingDeweyID(), null);
            } else {
              deweyID = nodeTrx.getParentDeweyID().getNewChildID();
            }
          } else {
            previousLevel++;
            deweyID = nodeTrx.getParentDeweyID().getNewChildID();
          }
        }

        final Node node =
            (Node) pageTrx.prepareEntryForModification(nodeTrx.getNodeKey(),
                PageKind.RECORDPAGE, -1);
        node.setDeweyID(deweyID);
      }

      nodeTrx.moveTo(nodeKey);
    }
  }

  /**
   * Get an optional namespace {@link SirixDeweyID} reference.
   *
   * @return optional namespace {@link SirixDeweyID} reference
   * @throws SirixException if generating an ID fails
   */
  SirixDeweyID newNamespaceID() {
    SirixDeweyID id = null;
    if (nodeTrx.storeDeweyIDs()) {
      if (nodeTrx.hasNamespaces()) {
        nodeTrx.moveToNamespace(nodeTrx.getNamespaceCount() - 1);
        id = SirixDeweyID.newBetween(nodeTrx.getDeweyID(), null);
        nodeTrx.moveToParent();
      } else {
        id = nodeTrx.getDeweyID().getNewNamespaceID();
      }
    }
    return id;
  }

  /**
   * Get an optional attribute {@link SirixDeweyID} reference.
   *
   * @return optional attribute {@link SirixDeweyID} reference
   * @throws SirixException if generating an ID fails
   */
  SirixDeweyID newAttributeID() {
    SirixDeweyID id = null;
    if (nodeTrx.storeDeweyIDs()) {
      if (nodeTrx.hasAttributes()) {
        nodeTrx.moveToAttribute(nodeTrx.getAttributeCount() - 1);
        id = SirixDeweyID.newBetween(nodeTrx.getDeweyID(), null);
        nodeTrx.moveToParent();
      } else {
        id = nodeTrx.getDeweyID().getNewAttributeID();
      }
    }
    return id;
  }

  /**
   * Get an optional first child {@link SirixDeweyID} reference.
   *
   * @return optional first child {@link SirixDeweyID} reference
   * @throws SirixException if generating an ID fails
   */
  SirixDeweyID newFirstChildID() {
    SirixDeweyID id = null;
    if (nodeTrx.storeDeweyIDs()) {
      if (nodeTrx.hasFirstChild()) {
        nodeTrx.moveToFirstChild();
        id = SirixDeweyID.newBetween(null, nodeTrx.getDeweyID());
      } else {
        id = nodeTrx.getDeweyID().getNewChildID();
      }
    }
    return id;
  }

  /**
   * Get an optional left sibling {@link SirixDeweyID} reference.
   *
   * @return optional left sibling {@link SirixDeweyID} reference
   * @throws SirixException if generating an ID fails
   */
  SirixDeweyID newLeftSiblingID() {
    SirixDeweyID id = null;
    if (nodeTrx.storeDeweyIDs()) {
      final SirixDeweyID currID = nodeTrx.getDeweyID();
      if (nodeTrx.hasLeftSibling()) {
        nodeTrx.moveToLeftSibling();
        id = SirixDeweyID.newBetween(nodeTrx.getDeweyID(), currID);
        nodeTrx.moveToRightSibling();
      } else {
        id = SirixDeweyID.newBetween(null, currID);
      }
    }
    return id;
  }

  /**
   * Get an optional right sibling {@link SirixDeweyID} reference.
   *
   * @return optional right sibling {@link SirixDeweyID} reference
   * @throws SirixException if generating an ID fails
   */
  SirixDeweyID newRightSiblingID() {
    SirixDeweyID id = null;
    if (nodeTrx.storeDeweyIDs()) {
      final SirixDeweyID currID = nodeTrx.getDeweyID();
      if (nodeTrx.hasRightSibling()) {
        nodeTrx.moveToRightSibling();
        id = SirixDeweyID.newBetween(currID, nodeTrx.getDeweyID());
        nodeTrx.moveToLeftSibling();
      } else {
        id = SirixDeweyID.newBetween(currID, null);
      }
    }
    return id;
  }
}