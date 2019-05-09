package util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageUtil {
    private StorageReference uploadToStorage;

    public FirebaseStorageUtil(){
        this.uploadToStorage = FirebaseStorage.getInstance().getReference("user_uploads").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
      /*  try{
            currentUserReference = uploadToStorage.getReference(FirebaseAuth.getInstance().getUid());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }*/
    }

    public StorageReference getUserStorageRef(){
        return  uploadToStorage;
    }

    public StorageReference getInventoryStorageRef(){
        return uploadToStorage.child("inventories");
    }

}
