package gleam.docservice;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * A SoftReference that holds a label as well as a soft referent. Useful
 * when using soft references in a Map (where the label would be the key
 * that the reference is stored under in the map).
 * 
 * @author ian
 * 
 * @param <L> the type of the label
 * @param <T> the type of the referent of the soft reference
 */
public class LabelledSoftReference<L, T> extends SoftReference<T> {

  private L label;

  public LabelledSoftReference(L label, T referent) {
    super(referent);
    this.label = label;
  }

  public LabelledSoftReference(L label, T referent, ReferenceQueue<? super T> q) {
    super(referent, q);
    this.label = label;
  }

  public L getLabel() {
    return label;
  }
}
