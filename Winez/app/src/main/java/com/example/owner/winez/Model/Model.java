package com.example.owner.winez.Model;

import android.graphics.Bitmap;
import com.example.owner.winez.Utils.ApiClasses.WineApiClass;
import com.example.owner.winez.Utils.ModelSQL.UserSQL;
import com.example.owner.winez.Utils.ModelSQL.WineSQL;
import com.example.owner.winez.Utils.WinesLocalDB;
import com.example.owner.winez.Utils.WinezAuth;
import com.example.owner.winez.Utils.WinezDB;
import com.example.owner.winez.Utils.WinezStorage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.List;

/**
 * Created by owner on 01-Feb-17.
 */


public class Model {
    private static Model instance;
    WinezDB mRemoteDB;
    WinesLocalDB modelLocalSql;

    private Model() {
        modelLocalSql = WinesLocalDB.getInstance();
        mRemoteDB = WinezDB.getInstance();
    }

    public static Model getInstance(){
        if (instance == null){
            instance = new Model();
        }
        return instance ;
    }

    /**
     * Get a wine by wine uid
     * @param id
     * @param getOnCompleteResult
     */
    public void getWine(String id, WinezDB.GetOnCompleteResult<Wine> getOnCompleteResult){
        this.mRemoteDB.getSingle(Wine.class.getSimpleName(), Wine.class,id, getOnCompleteResult);
    }

    public void getCommentsForWine(String wineId,
                                   WinezDB.OnChildEventListener<Comment> getOnCompleteResult){
        this.mRemoteDB.getUpdatesForChildren(Comment.class.getSimpleName(), Comment.class, wineId, getOnCompleteResult);
    }

    public Task<Void> saveCurrentUser(User toSave){
        // Save to local - save user and his wines
        //saveCurrentUserLocal(toSave);
        return this.mRemoteDB.saveWithId(User.class.getSimpleName(),toSave);
    }

    public void saveCurrentUserLocal(User toSave){
        // Save to local - save user and his wines
        UserSQL.getInstance().addEntity(this.modelLocalSql.getReadbleDB(), toSave);
        WineSQL.getInstance().deleteAll(this.modelLocalSql.getReadbleDB());
        WineSQL.getInstance().add(this.modelLocalSql.getReadbleDB(),toSave.getUserWines());
    }


    public Task<Void> saveComment(Comment cmt){
        return this.mRemoteDB.saveChildWithoutId(Comment.class.getSimpleName(), cmt.getWineID(), cmt);
    }

    public Task<AuthResult> authenticate(String email, String password) {
        return WinezAuth.getInstance().authenticate(email,password);
    }

    public void signOut() {
        WineSQL.getInstance().deleteAll(this.modelLocalSql.getReadbleDB());
        UserSQL.getInstance().deleteAll(this.modelLocalSql.getReadbleDB());
        WinezAuth.getInstance().signOut();


    }

    public void setOnAuthChangeListener(WinezAuth.OnAuthChangeListener onAuthChangeListener){
        WinezAuth.getInstance().setOnAuthChangeListener(onAuthChangeListener);
    }

    public boolean isAuthenticated() {
        return WinezAuth.getInstance().isAuthenticated();
    }

    public User getCurrentUser() {

        return WinezAuth.getInstance().getCurrentUser();
    }
    public User getCurrentUserLocal() {

        User toReturn = UserSQL.getInstance().getUser(this.modelLocalSql.getReadbleDB(), WinezAuth.getInstance().getUserUid());
        if (toReturn != null) {
            List<Wine> wines = WineSQL.getInstance().getAllEntities(this.modelLocalSql.getReadbleDB());

            for (Wine wine : wines) {
                toReturn.getUserWines().put(wine.getUid(), wine.getName());
            }

            WinezAuth.getInstance().setUserFromLocal(toReturn);
        }
        return toReturn;
    }

    /**
     * Adds wine to user list
     * @param wineToAdd
     */
    public void addWine(Wine wineToAdd)
    {
        this.getCurrentUser().getUserWines().put(wineToAdd.getUid(), wineToAdd.getName());
        saveCurrentUser();
        this.saveWine(wineToAdd);
        WineSQL.getInstance().addEntity(modelLocalSql.getWritableDB(),wineToAdd);
    }

    /**
     * Adds wine to db without adding it to user wine list
     * @param wineToAdd
     */
    public void addWineToDB(WineApiClass wineToAdd)
    {
        this.saveWine(new Wine(wineToAdd));
    }


    public void removeWine(WineApiClass toRemove){
        this.getCurrentUser().getUserWines().remove(toRemove.getId());
        // Saving to local and remote
        WineSQL.getInstance().delete(this.modelLocalSql.getWritableDB(),toRemove.getId());
        this.saveCurrentUser();
    }

    public void saveImage(Bitmap image, String url, WinezStorage.OnSaveCompleteListener onSaveCompleteListener) {
        WinezStorage.getInstance().saveImage(image,url,onSaveCompleteListener);
    }

    public void saveCurrentUser() {
        this.saveCurrentUser(this.getCurrentUser());
    }

    public void saveWine(Wine wine) {
        WinezDB.getInstance().saveWithId(Wine.class.getSimpleName(), wine);
    }

    public void getImage(String picture, WinezStorage.OnGetBitmapListener onGetBitmapListener) {
        WinezStorage.getInstance().getImage(picture,onGetBitmapListener);
    }

    public Wine getWineFromLocal(String id){
        return WineSQL.getInstance().getWine(this.modelLocalSql.getReadbleDB(), id);
    }
}
