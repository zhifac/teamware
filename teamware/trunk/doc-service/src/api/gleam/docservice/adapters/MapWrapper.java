package gleam.docservice.adapters;

import java.util.List;

public class MapWrapper<K, V> {
  private List<MapEntry<K, V>> entries;

  public List<MapEntry<K, V>> getEntries() {
    return entries;
  }

  public void setEntries(List<MapEntry<K, V>> entries) {
    this.entries = entries;
  }
}
