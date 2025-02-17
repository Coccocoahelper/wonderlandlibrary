package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongByteHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TLongByteHashMap extends TLongByteHash implements TLongByteMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient byte[] _values;

   public TLongByteHashMap() {
   }

   public TLongByteHashMap(int var1) {
      super(var1);
   }

   public TLongByteHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TLongByteHashMap(int var1, float var2, long var3, byte var5) {
      super(var1, var2, var3, var5);
   }

   public TLongByteHashMap(long[] var1, byte[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TLongByteHashMap(TLongByteMap var1) {
      super(var1.size());
      if (var1 instanceof TLongByteHashMap) {
         TLongByteHashMap var2 = (TLongByteHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0L) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._values = new byte[var2];
      return var2;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      long[] var3 = this._set;
      byte[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new long[var1];
      this._values = new byte[var1];
      this._states = new byte[var1];
      int var6 = var2;

      while(var6-- > 0) {
         if (var5[var6] == 1) {
            long var7 = var3[var6];
            int var9 = this.insertKey(var7);
            this._values[var9] = var4[var6];
         }
      }

   }

   public byte put(long var1, byte var3) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var3, var4);
   }

   public byte putIfAbsent(long var1, byte var3) {
      int var4 = this.insertKey(var1);
      return var4 < 0 ? this._values[-var4 - 1] : this.doPut(var1, var3, var4);
   }

   private byte doPut(long var1, byte var3, int var4) {
      byte var5 = this.no_entry_value;
      boolean var6 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var6 = false;
      }

      this._values[var4] = var3;
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Long)var3.getKey(), (Byte)var3.getValue());
      }

   }

   public void putAll(TLongByteMap var1) {
      this.ensureCapacity(var1.size());
      TLongByteIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public byte get(long var1) {
      int var3 = this.index(var1);
      return var3 < 0 ? this.no_entry_value : this._values[var3];
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
   }

   public boolean isEmpty() {
      return 0 == this._size;
   }

   public byte remove(long var1) {
      byte var3 = this.no_entry_value;
      int var4 = this.index(var1);
      if (var4 >= 0) {
         var3 = this._values[var4];
         this.removeAt(var4);
      }

      return var3;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public TLongSet keySet() {
      return new TLongByteHashMap.TKeyView(this);
   }

   public long[] keys() {
      long[] var1 = new long[this.size()];
      long[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public long[] keys(long[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new long[var2];
      }

      long[] var3 = this._set;
      byte[] var4 = this._states;
      int var5 = var3.length;
      int var6 = 0;

      while(var5-- > 0) {
         if (var4[var5] == 1) {
            var1[var6++] = var3[var5];
         }
      }

      return var1;
   }

   public TByteCollection valueCollection() {
      return new TLongByteHashMap.TValueView(this);
   }

   public byte[] values() {
      byte[] var1 = new byte[this.size()];
      byte[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public byte[] values(byte[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new byte[var2];
      }

      byte[] var3 = this._values;
      byte[] var4 = this._states;
      int var5 = var3.length;
      int var6 = 0;

      while(var5-- > 0) {
         if (var4[var5] == 1) {
            var1[var6++] = var3[var5];
         }
      }

      return var1;
   }

   public boolean containsValue(byte var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var2[var4] != 1 || var1 != var3[var4]);

      return true;
   }

   public boolean containsKey(long var1) {
      return this.contains(var1);
   }

   public TLongByteIterator iterator() {
      return new TLongByteHashMap.TLongByteHashIterator(this, this);
   }

   public boolean forEachKey(TLongProcedure var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(TByteProcedure var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(TLongByteProcedure var1) {
      byte[] var2 = this._states;
      long[] var3 = this._set;
      byte[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues(TByteFunction var1) {
      byte[] var2 = this._states;
      byte[] var3 = this._values;
      int var4 = var3.length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.execute(var3[var4]);
         }
      }

   }

   public boolean retainEntries(TLongByteProcedure var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      long[] var4 = this._set;
      byte[] var5 = this._values;
      this.tempDisableAutoCompaction();
      int var6 = var4.length;

      while(var6-- > 0) {
         if (var3[var6] == 1 && !var1.execute(var4[var6], var5[var6])) {
            this.removeAt(var6);
            var2 = true;
         }
      }

      this.reenableAutoCompaction(true);
      return var2;
   }

   public boolean increment(long var1) {
      return this.adjustValue(var1, (byte)1);
   }

   public boolean adjustValue(long var1, byte var3) {
      int var4 = this.index(var1);
      if (var4 < 0) {
         return false;
      } else {
         byte[] var10000 = this._values;
         var10000[var4] += var3;
         return true;
      }
   }

   public byte adjustOrPutValue(long var1, byte var3, byte var4) {
      int var5 = this.insertKey(var1);
      boolean var6;
      byte var7;
      if (var5 < 0) {
         var5 = -var5 - 1;
         byte[] var10000 = this._values;
         var7 = var10000[var5] += var3;
         var6 = false;
      } else {
         var7 = this._values[var5] = var4;
         var6 = true;
      }

      byte var9 = this._states[var5];
      if (var6) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var7;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TLongByteMap)) {
         return false;
      } else {
         TLongByteMap var2 = (TLongByteMap)var1;
         if (var2.size() != this.size()) {
            return false;
         } else {
            byte[] var3 = this._values;
            byte[] var4 = this._states;
            byte var5 = this.getNoEntryValue();
            byte var6 = var2.getNoEntryValue();
            int var7 = var3.length;

            while(var7-- > 0) {
               if (var4[var7] == 1) {
                  long var8 = this._set[var7];
                  byte var10 = var2.get(var8);
                  byte var11 = var3[var7];
                  if (var11 != var10 && var11 != var5 && var10 != var6) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      byte[] var2 = this._states;
      int var3 = this._values.length;

      while(var3-- > 0) {
         if (var2[var3] == 1) {
            var1 += HashFunctions.hash(this._set[var3]) ^ HashFunctions.hash(this._values[var3]);
         }
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TLongByteProcedure(this, var1) {
         private boolean first;
         final StringBuilder val$buf;
         final TLongByteHashMap this$0;

         {
            this.this$0 = var1;
            this.val$buf = var2;
            this.first = true;
         }

         public boolean execute(long var1, byte var3) {
            if (this.first) {
               this.first = false;
            } else {
               this.val$buf.append(", ");
            }

            this.val$buf.append(var1);
            this.val$buf.append("=");
            this.val$buf.append(var3);
            return true;
         }
      });
      var1.append("}");
      return var1.toString();
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._states.length;

      while(var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.writeLong(this._set[var2]);
            var1.writeByte(this._values[var2]);
         }
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      super.readExternal(var1);
      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         long var3 = var1.readLong();
         byte var5 = var1.readByte();
         this.put(var3, var5);
      }

   }

   static long access$000(TLongByteHashMap var0) {
      return var0.no_entry_key;
   }

   static int access$100(TLongByteHashMap var0) {
      return var0._size;
   }

   static int access$200(TLongByteHashMap var0) {
      return var0._size;
   }

   static byte access$300(TLongByteHashMap var0) {
      return var0.no_entry_value;
   }

   static byte access$400(TLongByteHashMap var0) {
      return var0.no_entry_value;
   }

   static int access$500(TLongByteHashMap var0) {
      return var0._size;
   }

   static int access$600(TLongByteHashMap var0) {
      return var0._size;
   }

   class TLongByteHashIterator extends THashPrimitiveIterator implements TLongByteIterator {
      final TLongByteHashMap this$0;

      TLongByteHashIterator(TLongByteHashMap var1, TLongByteHashMap var2) {
         super(var2);
         this.this$0 = var1;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public long key() {
         return this.this$0._set[this._index];
      }

      public byte value() {
         return this.this$0._values[this._index];
      }

      public byte setValue(byte var1) {
         byte var2 = this.value();
         this.this$0._values[this._index] = var1;
         return var2;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            this._hash.tempDisableAutoCompaction();
            this.this$0.removeAt(this._index);
            this._hash.reenableAutoCompaction(false);
            --this._expectedSize;
         }
      }
   }

   class TLongByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
      final TLongByteHashMap this$0;

      TLongByteValueHashIterator(TLongByteHashMap var1, TPrimitiveHash var2) {
         super(var2);
         this.this$0 = var1;
      }

      public byte next() {
         this.moveToNextIndex();
         return this.this$0._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            this._hash.tempDisableAutoCompaction();
            this.this$0.removeAt(this._index);
            this._hash.reenableAutoCompaction(false);
            --this._expectedSize;
         }
      }
   }

   class TLongByteKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {
      final TLongByteHashMap this$0;

      TLongByteKeyHashIterator(TLongByteHashMap var1, TPrimitiveHash var2) {
         super(var2);
         this.this$0 = var1;
      }

      public long next() {
         this.moveToNextIndex();
         return this.this$0._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            this._hash.tempDisableAutoCompaction();
            this.this$0.removeAt(this._index);
            this._hash.reenableAutoCompaction(false);
            --this._expectedSize;
         }
      }
   }

   protected class TValueView implements TByteCollection {
      final TLongByteHashMap this$0;

      protected TValueView(TLongByteHashMap var1) {
         this.this$0 = var1;
      }

      public TByteIterator iterator() {
         return this.this$0.new TLongByteValueHashIterator(this.this$0, this.this$0);
      }

      public byte getNoEntryValue() {
         return TLongByteHashMap.access$400(this.this$0);
      }

      public int size() {
         return TLongByteHashMap.access$500(this.this$0);
      }

      public boolean isEmpty() {
         return 0 == TLongByteHashMap.access$600(this.this$0);
      }

      public boolean contains(byte var1) {
         return this.this$0.containsValue(var1);
      }

      public byte[] toArray() {
         return this.this$0.values();
      }

      public byte[] toArray(byte[] var1) {
         return this.this$0.values(var1);
      }

      public boolean add(byte var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsAll(Collection var1) {
         Iterator var2 = var1.iterator();

         byte var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            Object var3 = var2.next();
            if (!(var3 instanceof Byte)) {
               return false;
            }

            var4 = (Byte)var3;
         } while(this.this$0.containsValue(var4));

         return false;
      }

      public boolean containsAll(TByteCollection var1) {
         TByteIterator var2 = var1.iterator();

         do {
            if (!var2.hasNext()) {
               return true;
            }
         } while(this.this$0.containsValue(var2.next()));

         return false;
      }

      public boolean containsAll(byte[] var1) {
         byte[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            if (!this.this$0.containsValue(var5)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(byte[] var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection var1) {
         boolean var2 = false;
         TByteIterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      public boolean retainAll(TByteCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TByteIterator var3 = this.iterator();

            while(var3.hasNext()) {
               if (!var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public boolean retainAll(byte[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         byte[] var3 = this.this$0._values;
         byte[] var4 = this.this$0._states;
         int var5 = var3.length;

         while(var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               this.this$0.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      public boolean removeAll(Collection var1) {
         boolean var2 = false;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof Byte) {
               byte var5 = (Byte)var4;
               if (var5 > 0) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      public boolean removeAll(TByteCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TByteIterator var3 = var1.iterator();

            while(var3.hasNext()) {
               byte var4 = var3.next();
               if (var4 > 0) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public boolean removeAll(byte[] var1) {
         boolean var2 = false;
         int var3 = var1.length;

         while(var3-- > 0) {
            if (var1[var3] > 0) {
               var2 = true;
            }
         }

         return var2;
      }

      public void clear() {
         this.this$0.clear();
      }

      public boolean forEach(TByteProcedure var1) {
         return this.this$0.forEachValue(var1);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("{");
         this.this$0.forEachValue(new TByteProcedure(this, var1) {
            private boolean first;
            final StringBuilder val$buf;
            final TLongByteHashMap.TValueView this$1;

            {
               this.this$1 = var1;
               this.val$buf = var2;
               this.first = true;
            }

            public boolean execute(byte var1) {
               if (this.first) {
                  this.first = false;
               } else {
                  this.val$buf.append(", ");
               }

               this.val$buf.append(var1);
               return true;
            }
         });
         var1.append("}");
         return var1.toString();
      }
   }

   protected class TKeyView implements TLongSet {
      final TLongByteHashMap this$0;

      protected TKeyView(TLongByteHashMap var1) {
         this.this$0 = var1;
      }

      public TLongIterator iterator() {
         return this.this$0.new TLongByteKeyHashIterator(this.this$0, this.this$0);
      }

      public long getNoEntryValue() {
         return TLongByteHashMap.access$000(this.this$0);
      }

      public int size() {
         return TLongByteHashMap.access$100(this.this$0);
      }

      public boolean isEmpty() {
         return 0 == TLongByteHashMap.access$200(this.this$0);
      }

      public boolean contains(long var1) {
         return this.this$0.contains(var1);
      }

      public long[] toArray() {
         return this.this$0.keys();
      }

      public long[] toArray(long[] var1) {
         return this.this$0.keys(var1);
      }

      public boolean add(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean containsAll(Collection var1) {
         Iterator var2 = var1.iterator();

         long var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            Object var3 = var2.next();
            if (!(var3 instanceof Long)) {
               return false;
            }

            var4 = (Long)var3;
         } while(this.this$0.containsKey(var4));

         return false;
      }

      public boolean containsAll(TLongCollection var1) {
         TLongIterator var2 = var1.iterator();

         do {
            if (!var2.hasNext()) {
               return true;
            }
         } while(this.this$0.containsKey(var2.next()));

         return false;
      }

      public boolean containsAll(long[] var1) {
         long[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            long var5 = var2[var4];
            if (!this.this$0.contains(var5)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TLongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long[] var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection var1) {
         boolean var2 = false;
         TLongIterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      public boolean retainAll(TLongCollection var1) {
         if (this == var1) {
            return false;
         } else {
            boolean var2 = false;
            TLongIterator var3 = this.iterator();

            while(var3.hasNext()) {
               if (!var1.contains(var3.next())) {
                  var3.remove();
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public boolean retainAll(long[] var1) {
         boolean var2 = false;
         Arrays.sort(var1);
         long[] var3 = this.this$0._set;
         byte[] var4 = this.this$0._states;
         int var5 = var3.length;

         while(var5-- > 0) {
            if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
               this.this$0.removeAt(var5);
               var2 = true;
            }
         }

         return var2;
      }

      public boolean removeAll(Collection var1) {
         boolean var2 = false;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof Long) {
               long var5 = (Long)var4;
               if (this != var5) {
                  var2 = true;
               }
            }
         }

         return var2;
      }

      public boolean removeAll(TLongCollection var1) {
         if (this == var1) {
            this.clear();
            return true;
         } else {
            boolean var2 = false;
            TLongIterator var3 = var1.iterator();

            while(var3.hasNext()) {
               long var4 = var3.next();
               if (this != var4) {
                  var2 = true;
               }
            }

            return var2;
         }
      }

      public boolean removeAll(long[] var1) {
         boolean var2 = false;
         int var3 = var1.length;

         while(var3-- > 0) {
            if (this != var1[var3]) {
               var2 = true;
            }
         }

         return var2;
      }

      public void clear() {
         this.this$0.clear();
      }

      public boolean forEach(TLongProcedure var1) {
         return this.this$0.forEachKey(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof TLongSet)) {
            return false;
         } else {
            TLongSet var2 = (TLongSet)var1;
            if (var2.size() != this.size()) {
               return false;
            } else {
               int var3 = this.this$0._states.length;

               do {
                  if (var3-- <= 0) {
                     return true;
                  }
               } while(this.this$0._states[var3] != 1 || var2.contains(this.this$0._set[var3]));

               return false;
            }
         }
      }

      public int hashCode() {
         int var1 = 0;
         int var2 = this.this$0._states.length;

         while(var2-- > 0) {
            if (this.this$0._states[var2] == 1) {
               var1 += HashFunctions.hash(this.this$0._set[var2]);
            }
         }

         return var1;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("{");
         this.this$0.forEachKey(new TLongProcedure(this, var1) {
            private boolean first;
            final StringBuilder val$buf;
            final TLongByteHashMap.TKeyView this$1;

            {
               this.this$1 = var1;
               this.val$buf = var2;
               this.first = true;
            }

            public boolean execute(long var1) {
               if (this.first) {
                  this.first = false;
               } else {
                  this.val$buf.append(", ");
               }

               this.val$buf.append(var1);
               return true;
            }
         });
         var1.append("}");
         return var1.toString();
      }
   }
}
