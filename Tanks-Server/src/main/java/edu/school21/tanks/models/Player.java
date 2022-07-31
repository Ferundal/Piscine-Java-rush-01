package edu.school21.tanks.models;

public class Player {
    private Long id;
    private int shots;
    private int hits;
    private int misses;

    public Player(Long id, int shots, int hits) {
        this.id = id;
        this.shots = shots;
        this.hits = hits;
        this.misses = shots - hits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getMisses() {
        return misses;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (shots != player.shots) return false;
        if (hits != player.hits) return false;
        if (misses != player.misses) return false;
        return id != null ? id.equals(player.id) : player.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + shots;
        result = 31 * result + hits;
        result = 31 * result + misses;
        return result;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", shots=" + shots +
                ", hits=" + hits +
                ", misses=" + misses +
                '}';
    }
}
