import java.util.ArrayList;

class Clan {

    private long id;
    private String name;
    private int gold;

    //добавление золота (здесь же отнимание, есл передать отрицательное число)
    public void incGold(int count)
    {
        gold+=count;
    }

    public long getId()
    {
        return id;
    }

    public int getGold()
    {
        return gold;
    }

    public Clan(long id, String name)
    {
        this.id=id;
        this.name=name;
        gold=0;
    }
}

class ClanManager {

    //список кланов в программе
    static ArrayList<Clan> clans = new ArrayList<Clan>();

    //получение клана по номеру
    public static Clan getClan(long clanId) {

        for(int i=0;i<clans.size();i++)
        {
            if(clans.get(i).getId()==clanId)
                return clans.get(i);
        }
        return null;
    }

    //добавление нового клана
    public static void AddClan(String name)
    {
        long newId = clans.size()+1;
        clans.add(new Clan(newId,name));
    }

    public static boolean saveClan(long clanId) {

        return false;

    }

    //получение информации о клане для вывода
    public static String getInfoClan(long clanId)
    {
        Clan c = ClanManager.getClan(clanId);
        String res = "ID клана: "+Long.toString(c.getId())+" Золото: "+ Long.toString(c.getGold())+"\n";
        return res;
    }

}



class ClanController {

    //функция добавления золота в клан по id
    public static void incGold(long clanId, int gold) {
        Clan clan = ClanManager.getClan(clanId);
        clan.incGold(gold);
    }

}

//запись для трекера
class User {

    long clanId;
    long userId;
    int gold;

    public User(long clanId, long userId, int gold)
    {
        this.clanId=clanId;
        this.userId=userId;
        this.gold = gold;
    }
}


class TrackerManager {

    // трекер с данными
    static ArrayList<User> tracker = new ArrayList<User>();

    //добавление в трекер записи
    public static void trackerClanGold(long clanId, long userId, int gold) {

        Clan clan = ClanManager.getClan(clanId);
        clan.incGold(gold);
        TrackerManager.trackGold(clanId, userId, gold);

    }

    public static void trackGold(long clanId, long userId, int gold)
    {
        tracker.add(new User(clanId,userId,gold));
    }

}

class Main{

    public static void main(String []args)
    {
//объект для создания синхронизации
        Object objectLock = new Object();
//добавляем один клан с именем - "Первый"
        ClanManager.AddClan("Первый");

//создаём и запускаем 1000 потоков (пытаемся создать, java сделает максимально возможное число и распараллелит между которыми смогла создать)
        for(int i=1;i<=1000;i++)
        {
            final int j=i;
//создание потока
            Thread myThready = new Thread(new Runnable()
            {
                //функция потока
                public void run()
                {
                    long userId = j;

//поток, который зашёл сюда первый, блокирует переменную objectLock и другие потоки вынуждены ждать
                    synchronized(objectLock){

//добавляем золото клану с id=1, золота + 10
                        ClanController.incGold(1, 10);
//вывод названия потока, номер действия, информации о текущем клане
                        System.out.println("Поток: " + Thread.currentThread().getName() +" Действие "+ Integer.toString(j) +":\n"+ClanManager.getInfoClan(1));


                    }

                }
            });
//запуск потока
            myThready.start();
        }

//ждём 10 секунд (примерное завершение всех потоков, чтобы узнать полную полученную сумму золота)
        try{
            Thread.sleep(20000);
            System.out.println("\nВ итоге:\n"+ClanManager.getInfoClan(1));
        }
        catch(Exception ex)
        {

        }

    }

}
