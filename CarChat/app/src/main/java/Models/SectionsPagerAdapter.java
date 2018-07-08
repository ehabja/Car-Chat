package Models;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jaber.carchat.Chat;
import com.example.jaber.carchat.Requests;
import com.example.jaber.carchat.Search;
import com.example.jaber.carchat.ryad;


public class SectionsPagerAdapter extends FragmentPagerAdapter{


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
//                ryad riyad = new ryad();
//                return riyad;
                Requests requests = new Requests();
                return requests;
            case 1:
                Search search = new Search();
                return search;
                //ryad riyad = new ryad();
                //return riyad;
            case 2:
                Chat chat = new Chat();
                return chat;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "SEARCH";
            case 2:
                return "MESSAGES";
                default:
                    return null;
        }
    }
}
