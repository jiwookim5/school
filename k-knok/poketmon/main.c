#include "poketmon.h"

int poketmon_load(t_data **list)
{
    int cnt;
    int i = 0;
    FILE *fd = fopen("poketmon.txt", "r");
    
    if (fd == NULL)
    {
        printf ("파일을 읽을 수 없습니다.\n");
        return(0);
    } 

    fscanf(fd, "%d", &cnt);
    *list = (t_data *)malloc(sizeof(t_data) * cnt);
    while(i < cnt)
    {
        fscanf(fd, "%s %s %d %d", (*list)[i].name, (*list)[i].type, &(*list)[i].min_attack, &(*list)[i].min_hp);
        i++;
    }
    fclose(fd);
    // i = 0;
    // while(i < cnt)
    // {
    //     printf("name = %s, type = %s, attack = %d, hp = %d\n", (*list)[i].name, 
    //             (*list)[i].type, 
    //             (*list)[i].min_attack, 
    //             (*list)[i].min_hp);
    //     i++;
    // }
    
    return(cnt);
}

void start_new_game(t_player_info *p, t_data *list, int cnt)
{
    int choice;
    int i = 0;

    printf("========================================\n");
    printf("어느 포켓몬을 선택하시겠습니까?\n");
    while (i < 3)
    {
        printf("%d. %s ", i + 1, list[i].name);
        i++;
    }
    printf("\n>> ");
    scanf("%d", &choice);
    choice--;

    strcpy(p->poketmon.name, list[choice].name);
    strcpy(p->poketmon.type, list[choice].type);

    p->poketmon.attack = list[choice].min_attack + (rand() % 101);
    p->poketmon.hp = list[choice].min_hp + (rand() % 151);
    p->money = 10000;

    printf("\n[ %s ]를 선택하셨습니다!\n", p->poketmon.name);
    printf("결정된 능력치 - 공격력: %d, 체력: %d\n", p->poketmon.attack, p->poketmon.hp);
    printf("트레이너의 지갑에 10,000원이 추가되었습니다.\n");
}

int change_poketmon(t_player_info *player)
{
    int i = 0;
    int choice;
    int alive_count = 0;

    printf("cnt === %d\n", alive_count);
    printf("어느 포켓몬을 내보낼까?\n");

    
    while(i < player->poket_cnt)
    {
        if (player->my_list[0].name[0] == '\0')
        {
            printf("00000000\n");
            break;
        }
        if(player->my_list[i].hp > 0)
        {
            printf("%d. %s %s %d/%d\n", i + 1, player->poketmon.name, player->poketmon.type,player->poketmon.hp, player->poketmon.my_full_hp);
            alive_count++;
            i++;
        }
    }
    if (alive_count == 0)
    {
        printf("눈앞이 깜깜해졌다...\n");
        player->money -= 1000;
        if(player->money < 0)
            player->money = 0;
        printf("현재 잔액 : %d\n", player->money);
        return(0);
    }
    while(i < player->poket_cnt)
    {
        if(player->my_list[i].hp > 0)
        {
            printf("%d. %s [%s] (HP: %d/%d)\n",
                i + 1,
                player->my_list[i].name,
                player->my_list[i].type,
                player->my_list[i].hp,
                player->my_list[i].my_full_hp);
        }
        i++;
    }
    printf(">> ");
    scanf("%d", &choice);
    if (choice <= 0) 
        return (0);
    choice--;
    if (choice < player->poket_cnt && player->my_list[choice].hp > 0)
    {
        player->poketmon = player->my_list[choice];
        return (1);
    }

    return(0);
}

void attack_menu(t_player_info *player, t_my *wild)
{
    int choice;
    int i = 0;

    scanf("%d", &choice);

    if (choice == 1)
    {
        int turn = rand() % 2;
        
        while (i < 2)
        {
            float add = 1.0;
            int damage;
            
            printf("========================================\n");
            
            if (turn == 0)
            {
                printf("%s의 공격!\n", player->poketmon.name);
                
                if ((strcmp(player->poketmon.type, "불") == 0 && strcmp(wild->type, "풀") == 0) ||
                    (strcmp(player->poketmon.type, "물") == 0 && strcmp(wild->type, "불") == 0) ||
                    (strcmp(player->poketmon.type, "풀") == 0 && strcmp(wild->type, "물") == 0))
                    add = 1.5;
                else if ((strcmp(player->poketmon.type, "불") == 0 && strcmp(wild->type, "물") == 0) ||
                         (strcmp(player->poketmon.type, "물") == 0 && strcmp(wild->type, "풀") == 0) ||
                         (strcmp(player->poketmon.type, "풀") == 0 && strcmp(wild->type, "불") == 0))
                    add = 0.5;

                damage = (int)(player->poketmon.attack * add);
                int crit = (rand() % 100 < 20);
                if (crit) damage *= 1.5;

                wild->hp -= damage;
                printf("%s는 %d의 데미지를 입었다.\n", wild->name, damage);
                
                if (add == 1.5) printf("효과가 굉장했다!\n");
                else if (add == 0.5) printf("효과가 별로인 듯 하다.\n");
                if (crit) printf("급소에 맞았다!\n");

                if (wild->hp <= 0) break;
                turn = 1;
            }
            else 
            {
                printf("%s의 공격!\n", wild->name);
                
                if ((strcmp(wild->type, "불") == 0 && strcmp(player->poketmon.type, "풀") == 0) ||
                    (strcmp(wild->type, "물") == 0 && strcmp(player->poketmon.type, "불") == 0) ||
                    (strcmp(wild->type, "풀") == 0 && strcmp(player->poketmon.type, "물") == 0))
                    add = 1.5;
                else if ((strcmp(wild->type, "불") == 0 && strcmp(player->poketmon.type, "물") == 0) ||
                         (strcmp(wild->type, "물") == 0 && strcmp(player->poketmon.type, "풀") == 0) ||
                         (strcmp(wild->type, "풀") == 0 && strcmp(player->poketmon.type, "불") == 0))
                    add = 0.5;

                damage = (int)(wild->attack * add);
                int crit = (rand() % 100 < 20);
                if (crit) damage *= 1.5;

                player->poketmon.hp -= damage;
                printf("%s는 %d의 데미지를 입었다.\n", player->poketmon.name, damage);

                if (add == 1.5) printf("효과가 굉장했다!\n");
                else if (add == 0.5) printf("효가과 별로인 듯 하다.\n");
                if (crit) printf("급소에 맞았다!\n");
                turn = 0;
            }
            i++;
        }
        if (player->poketmon.hp <= 0)
        {
            player->poketmon.hp = 0;
            printf("%s는 쓰러졌다.\n", player->poketmon.name);
            printf("\t\t\t\t\t     야생 포켓몬: %s \n\t\t\t\t\t     (HP: %d/%d)\n", \
            wild->name, wild->hp, wild->hp + 0);
    
            printf("\n내 포켓몬 (기절): %s \n(HP: %d/%d)\n", player->poketmon.name, \
            player->poketmon.hp, player->poketmon.my_full_hp);
            if (change_poketmon(player) == 1)
                printf("%s(이)가 새롭게 등장했다!\n", player->poketmon.name);
            else
                return;

        }
        if (wild->hp <= 0)
            printf("야ㅎ생 포켓몬 뒤짐\n");
    }
}


void play_adventure(t_player_info *player, t_data *list, int cnt)
{
    int i = rand() % cnt;
    t_my wild;

    printf("========================================\n");
    printf("포켓몬을 탐색하는중 . . .\n");

    int wait= (rand() % 5) + 1;
    sleep(wait);


    strcpy(wild.name, list[i].name);
    strcpy(wild.type, list[i].type);
    wild.attack = list[i].min_attack + (rand() % 101);
    wild.hp = list[i].min_hp + (rand() % 151);
    int wild_max_hp = wild.hp;
    player->poketmon.my_full_hp = player->poketmon.hp;
    
    printf("========================================\n");
    printf("앗! 야생의 %s이(가) 나타났다!\n", wild.name);
    printf("========================================\n");
    
    while(player->poketmon.hp >= 0 && wild.hp >= 0)
    {
        if (wild.hp < 0) 
            wild.hp = 0;

        printf("\t\t\t\t\t     야생 포켓몬: %s \n\t\t\t\t\t     (HP: %d/%d)\n", \
            wild.name, wild.hp, wild_max_hp);
        if (player->poketmon.hp == 0)
        {
            printf("\n내 포켓몬 (기절): %s \n(HP: %d/%d)\n", player->poketmon.name, \
                player->poketmon.hp, player->poketmon.my_full_hp);
            printf("어느 포켓몬을 내보낼까?\n");

        }
        else
            printf("\n내 포켓몬: %s \n(HP: %d/%d)\n", player->poketmon.name, \
                player->poketmon.hp, player->poketmon.my_full_hp);
        printf("========================================\n");
        printf("무엇을 해야할까?\n1. 공격 2. 가방열기 3. 도망치기\n>> ");
        printf("========================================\n");
        int slect;
        scanf("%d", &slect);
        if (slect == 1) 
            attack_menu(player, &wild);
    }

}

int main()
{
    srand(time(NULL));
    t_data *list = NULL;
    int cnt;
    t_player_info player;
    int menu;

    cnt = poketmon_load(&list);

    if (cnt == 0)
        return (1);
    printf("========================================\n");
    printf("       K. Knock Pokemon Game\n\n");
    printf("         press enter to start\n");
    printf("========================================\n");
    getchar();
    printf("========================================\n");
    printf("    1. 새로하기    2. 이어하기\n");
    printf(">> ");
    scanf("%d", &menu);

    if (menu == 1)
        start_new_game(&player, list, cnt);
    else
    {
        printf("이어하기 코드\n");
        return (1);
    }
    while(1)
    {
        printf("\n========================================\n");
        printf("모험을 진행하시겠습니까?\n");
        printf("1. 네  2. 저장  3. 상점  4. 포켓몬센터  5. 포켓몬도감\n");
        printf(">> ");
        int action;
        scanf("%d", &action);

        if (action == 1) 
        {
            play_adventure(&player, list, cnt);
        }  
            
        else if (action == 2) 
            printf("게임을 저장합니다.\n");
    }

    free(list);
    return 0;
    return (0);
}