/*
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sirix.page;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.openhft.chronicle.bytes.Bytes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.sirix.api.PageReadOnlyTrx;
import org.sirix.page.delegates.BitmapReferencesPage;
import org.sirix.page.delegates.ReferencesPage4;
import org.sirix.page.interfaces.Page;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * All Page types.
 */
public enum PageKind {
  /**
   * {@link KeyValueLeafPage}.
   */
  RECORDPAGE((byte) 1, KeyValueLeafPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new KeyValueLeafPage(source, pageReadTrx);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadOnlyTrx, final Bytes<ByteBuffer> sink, final Page page,
        final SerializationType type) {
      sink.writeByte(RECORDPAGE.id);
      page.serialize(pageReadOnlyTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page nodePage, final PageReadOnlyTrx pageReadTrx) {
      assert nodePage instanceof KeyValueLeafPage;
      final KeyValueLeafPage page = (KeyValueLeafPage) nodePage;
      return new KeyValueLeafPage(page.getPageKey(), page.getIndexType(), pageReadTrx);
    }
  },

  /**
   * {@link NamePage}.
   */
  NAMEPAGE((byte) 2, NamePage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new NamePage(source, type);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        final SerializationType type) {
      sink.writeByte(NAMEPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new NamePage();
    }
  },

  /**
   * {@link UberPage}.
   */
  UBERPAGE((byte) 3, UberPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new UberPage(source);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        final SerializationType type) {
      sink.writeByte(UBERPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new UberPage();
    }
  },

  /**
   * {@link IndirectPage}.
   */
  INDIRECTPAGE((byte) 4, IndirectPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new IndirectPage(source, type);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        final SerializationType type) {
      sink.writeByte(INDIRECTPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new IndirectPage();
    }
  },

  /**
   * {@link RevisionRootPage}.
   */
  REVISIONROOTPAGE((byte) 5, RevisionRootPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new RevisionRootPage(source, type);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        final SerializationType type) {
      sink.writeByte(REVISIONROOTPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new RevisionRootPage();
    }
  },

  /**
   * {@link PathSummaryPage}.
   */
  PATHSUMMARYPAGE((byte) 6, PathSummaryPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final @NonNull SerializationType type) {
      return new PathSummaryPage(source, type);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        final @NonNull SerializationType type) {
      sink.writeByte(PATHSUMMARYPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new PathSummaryPage();
    }
  },

  /**
   * {@link CASPage}.
   */
  CASPAGE((byte) 8, CASPage.class) {
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
                                  final SerializationType type) {

      //source, bytes to read
      //type, kind of data
      Page delegate = PageUtils.createDelegate(source, type);

      final int  maxNodeKeySize = source.readInt();
      Int2LongMap maxNodeKeys = new Int2LongOpenHashMap((int) Math.ceil(maxNodeKeySize / 0.75));

      for (int i = 0; i < maxNodeKeySize; i++) {
        maxNodeKeys.put(i, source.readLong());
      }

      final int currentMaxLevelOfIndirectPages = source.readInt();
      Int2IntMap currentMaxLevelsOfIndirectPages = new Int2IntOpenHashMap((int) Math.ceil(currentMaxLevelOfIndirectPages / 0.75));

      for (int i = 0; i < currentMaxLevelOfIndirectPages; i++) {
        currentMaxLevelsOfIndirectPages.put(i, source.readByte() & 0xFF);
      }

      return new CASPage(delegate, maxNodeKeys, currentMaxLevelsOfIndirectPages);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
                       final SerializationType type) {

      CASPage casPage = (CASPage) page;
      Page delegate = casPage.delegate();
      sink.writeByte(CASPAGE.id);

      if (delegate instanceof ReferencesPage4) {
        sink.writeByte((byte) 0);
      } else if (delegate instanceof BitmapReferencesPage) {
        sink.writeByte((byte) 1);
      }
      delegate.serialize(pageReadTrx, sink, type);

      final int  maxNodeKeySize =  casPage.getMaxNodeKeySize();
      sink.writeInt(maxNodeKeySize);
      for (int i = 0; i < maxNodeKeySize; i++) {
        sink.writeLong(casPage.getMaxNodeKey(i));
      }

      final int currentMaxLevelOfIndirectPages = casPage.getCurrentMaxLevelOfIndirectPagesSize();
      sink.writeInt(currentMaxLevelOfIndirectPages);
      for (int i = 0; i < currentMaxLevelOfIndirectPages; i++) {
        sink.writeByte((byte) casPage.getCurrentMaxLevelOfIndirectPages(i));
      }
    }


    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new CASPage();
    }
  },

  /**
   * {@link OverflowPage}.
   */
  OVERFLOWPAGE((byte) 9, OverflowPage.class) {
    @Override
    @NonNull Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
        final SerializationType type) {
      return new OverflowPage(source);
    }

    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<ByteBuffer> sink, final Page page,
        @NonNull SerializationType type) {
      sink.writeByte(OVERFLOWPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    public @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx) {
      return new OverflowPage();
    }
  },

  /**
   * {@link PathPage}.
   */
  PATHPAGE((byte) 10, PathPage.class) {
    @Override
    void serializePage(final PageReadOnlyTrx pageReadTrx, Bytes<ByteBuffer> sink, @NonNull Page page,
        @NonNull SerializationType type) {
      sink.writeByte(PATHPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    Page deserializePage(@NonNull PageReadOnlyTrx pageReadTrx, Bytes<?> source,
        @NonNull SerializationType type) {
      return new PathPage(source, type);
    }

    @Override
    public @NonNull Page getInstance(Page page, @NonNull PageReadOnlyTrx pageReadTrx) {
      return new PathPage();
    }
  },

  /**
   * {@link PathPage}.
   */
  DEWEYIDPAGE((byte) 11, DeweyIDPage.class) {
    @Override
    void serializePage(@NonNull PageReadOnlyTrx pageReadTrx, Bytes<ByteBuffer> sink, @NonNull Page page,
        @NonNull SerializationType type) {
      sink.writeByte(DEWEYIDPAGE.id);
      page.serialize(pageReadTrx, sink, type);
    }

    @Override
    Page deserializePage(@NonNull PageReadOnlyTrx pageReadTrx, Bytes<?> source,
        @NonNull SerializationType type) {
      return new DeweyIDPage(source, type);
    }

    @Override
    public @NonNull Page getInstance(Page page, @NonNull PageReadOnlyTrx pageReadTrx) {
      return new DeweyIDPage();
    }
  };

  /**
   * Mapping of keys -> page
   */
  private static final Map<Byte, PageKind> INSTANCEFORID = new HashMap<>();

  /**
   * Mapping of class -> page.
   */
  private static final Map<Class<? extends Page>, PageKind> INSTANCEFORCLASS = new HashMap<>();

  static {
    for (final PageKind page : values()) {
      INSTANCEFORID.put(page.id, page);
      INSTANCEFORCLASS.put(page.clazz, page);
    }
  }

  /**
   * Unique ID.
   */
  private final byte id;

  /**
   * Class.
   */
  private final Class<? extends Page> clazz;

  /**
   * Constructor.
   *
   * @param id    unique identifier
   * @param clazz class
   */
  PageKind(final byte id, final Class<? extends Page> clazz) {
    this.id = id;
    this.clazz = clazz;
  }

  /**
   * Get the unique page ID.
   *
   * @return unique page ID
   */
  public byte getID() {
    return id;
  }

  /**
   * Serialize page.
   *
   * @param pageReadOnlyTrx the read only page transaction
   * @param sink            {@link Bytes<ByteBuffer>} instance
   * @param page            {@link Page} implementation
   */
  abstract void serializePage(final PageReadOnlyTrx pageReadOnlyTrx, final Bytes<ByteBuffer> sink, final Page page,
      final SerializationType type);

  /**
   * Deserialize page.
   *
   * @param pageReadTrx the read only page transaction
   * @param source      {@link Bytes<ByteBuffer>} instance
   * @return page instance implementing the {@link Page} interface
   */
  abstract Page deserializePage(final PageReadOnlyTrx pageReadTrx, final Bytes<?> source,
      final SerializationType type);

  /**
   * Public method to get the related page based on the identifier.
   *
   * @param id the identifier for the page
   * @return the related page
   */
  public static PageKind getKind(final byte id) {
    final PageKind page = INSTANCEFORID.get(id);
    if (page == null) {
      throw new IllegalStateException();
    }
    return page;
  }

  /**
   * Public method to get the related page based on the class.
   *
   * @param clazz the class for the page
   * @return the related page
   */
  public static @NonNull PageKind getKind(final Class<? extends Page> clazz) {
    final PageKind page = INSTANCEFORCLASS.get(clazz);
    if (page == null) {
      throw new IllegalStateException();
    }
    return page;
  }

  /**
   * New page instance.
   *
   * @param page        instance of class which implements {@link Page}
   * @param pageReadTrx instance of class which implements {@link PageReadOnlyTrx}
   * @return new page instance
   */
  public abstract @NonNull Page getInstance(final Page page, final PageReadOnlyTrx pageReadTrx);
}
