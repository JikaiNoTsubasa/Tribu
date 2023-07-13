package fr.triedge.tribu;

import com.idorsia.research.sbilib.utils.SPassword;

public class PWDManager {
    public static void main(String[] args) {
        if (args.length >=2){
            SPassword pwd = new SPassword(args[1]);
            if (args[0].equalsIgnoreCase("-encode")){
                System.out.println(pwd.getEncrypted());
            }else if (args[0].equalsIgnoreCase("-decode")){
                System.out.println(pwd.getDecrypted());
            }
        }
    }
}
