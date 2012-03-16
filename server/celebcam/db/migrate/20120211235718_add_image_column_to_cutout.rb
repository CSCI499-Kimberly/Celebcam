class AddImageColumnToCutout < ActiveRecord::Migration
 def self.up
    change_table :cutouts do |t|
      t.has_attached_file :image
    end
  end

  def self.down
    drop_attached_file :cutouts, :image
  end
end
