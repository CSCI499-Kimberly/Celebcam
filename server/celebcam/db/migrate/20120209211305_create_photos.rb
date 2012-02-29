class CreatePhotos < ActiveRecord::Migration
  def change
    create_table :photos do |t|
      t.string :image_link
      t.integer :cutout_id

      t.timestamps
    end
  end
end
