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
    return(cnt);
}

void    start_new_game(t_player_info *p, t_data *list, int cnt)
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
    p->monster_ball = 0;
    p->hiller = 0;
    p->poketmon.my_full_hp = p->poketmon.hp;
    p->my_list[0] = p->poketmon; 
    p->poket_cnt = 1;
    
    printf("\n[ %s ]를 선택하셨습니다!\n", p->poketmon.name);
    printf("결정된 능력치 - 공격력: %d, 체력: %d\n", p->poketmon.attack, p->poketmon.hp);
    printf("트레이너의 지갑에 10,000원이 추가되었습니다.\n");
}

void reset_player_data(t_player_info *p, t_data *list, int cnt)
{
    memset(p, 0, sizeof(t_player_info)); 
    start_new_game(p, list, cnt); 
}


int change_poketmon(t_player_info *player)
{
    int i = 0;
    int choice;
    int alive_count = 0;

    i = 0;
    while (i < player->poket_cnt)
    {
        if (strcmp(player->my_list[i].name, player->poketmon.name) == 0)
        {
            player->my_list[i].hp = player->poketmon.hp;
            break;
        }
        i++;
    }

    i = 0;
    alive_count = 0;
    while (i < player->poket_cnt)
    {
        if (player->my_list[i].hp > 0)
        {
            alive_count++;
        }
        i++;
    }

    // 3. [판단] 살아있는 포켓몬이 없으면 패배 처리 (메인으로 돌아감)
    if (alive_count == 0)
    {
        printf("\n========================================\n");
        printf("더 이상 싸울 수 있는 포켓몬이 없다!\n");
        printf("눈앞이 깜깜해졌다...\n");
        player->money -= 1000;
        if (player->money < 0) player->money = 0;
        printf("치료비로 1000원을 잃었습니다. (현재 잔액 : %d)\n", player->money);
        printf("========================================\n");
        return (0); 
    }

    while(1)
    {
        printf("\n어느 포켓몬을 내보낼까?\n");
        i = 0;
        
        while (i < player->poket_cnt)
        {

                printf("%d. %s [%s] (HP: %d/%d)\n",
                    i + 1 ,
                    player->my_list[i].name,
                    player->my_list[i].type,
                    player->my_list[i].hp,
                    player->my_list[i].my_full_hp);

            i++;
        }
        printf("0. 도망가기\n");
        printf(">> ");
        scanf("%d", &choice);
        if (choice <= 0)
        {
            printf("도망에 성공했다.\n");
            return (0);
        }
    
        int idx = choice - 1;
        if (idx >= 0 && idx < player->poket_cnt)
        {
            if(player->my_list[idx].hp > 0)
            {
                player->poketmon = player->my_list[idx];
                return (1);
            }
            else
                printf("\n[!] %s은(는) 이미 기절해서 나갈 수 없습니다!\n", player->my_list[idx].name);
        }
        else
            printf("잘못된 선택입니다.\n");

    }

}

void wild_attack(t_player_info *player, t_my *wild, float *add, int *damage)
{

    printf("%s의 공격!\n", wild->name);
    printf("fuckfuckfuckfuckfuck\n");
                
    if ((strcmp(wild->type, "불") == 0 && strcmp(player->poketmon.type, "풀") == 0) ||
        (strcmp(wild->type, "물") == 0 && strcmp(player->poketmon.type, "불") == 0) ||
        (strcmp(wild->type, "풀") == 0 && strcmp(player->poketmon.type, "물") == 0))
            *add = 1.5;
    else if ((strcmp(wild->type, "불") == 0 && strcmp(player->poketmon.type, "물") == 0) ||
            (strcmp(wild->type, "물") == 0 && strcmp(player->poketmon.type, "풀") == 0) ||
            (strcmp(wild->type, "풀") == 0 && strcmp(player->poketmon.type, "불") == 0))
                *add = 0.5;

    *damage = (int)(wild->attack * (*add));
    int crit = (rand() % 100 < 20);
    if (crit) *damage *= 1.5;

    player->poketmon.hp -= *damage;
    int i = 0;
    while (i < player->poket_cnt)
    {
        if(strcmp(player->my_list[i].name, player->poketmon.name) == 0)
        {
            player->my_list[i].hp = player->poketmon.hp;
            break;
        }
        i++;
    }

    printf("%s는 %d의 데미지를 입었다.\n", player->poketmon.name, *damage);

    if (*add == 1.5) 
        printf("효과가 굉장했다!\n");
    else if (*add == 0.5) 
        printf("효가과 별로인 듯 하다.\n");
    if (crit) 
        printf("급소에 맞았다!\n");
    
}

void my_poketmon_die(t_player_info *player, t_my *wild, int wild_max_hp)
{
    player->poketmon.hp = 0;
    if (wild->hp <= 0)
        wild->hp = 0;

    printf("%s는 쓰러졌다.\n", player->poketmon.name);
    printf("\t\t\t\t\t     야생 포켓몬: %s \n\t\t\t\t\t     (HP: %d/%d)\n", \
    wild->name, wild->hp, wild_max_hp);
    
    printf("\n내 포켓몬 (기절): %s \n(HP: %d/%d)\n", player->poketmon.name, \
    player->poketmon.hp, player->poketmon.my_full_hp);

}

void battle_poketmon(t_player_info *player, t_my *wild, int wild_max_hp)
{
    printf("\t\t\t\t\t     야생 포켓몬: %s \n\t\t\t\t\t     (HP: %d/%d)\n", \
                wild->name, wild->hp, wild_max_hp);
            if (player->poketmon.hp == 0)
            {
                printf("\n내 포켓몬 (기절): %s \n(HP: %d/%d)\n", player->poketmon.name, \
                    player->poketmon.hp, player->poketmon.my_full_hp);
    
            }
            else
                printf("\n내 포켓몬: %s \n(HP: %d/%d)\n", player->poketmon.name, \
                    player->poketmon.hp, player->poketmon.my_full_hp);
}

void attack_menu_choice_one(t_player_info *player, t_my *wild, int wild_max_hp)
{
    int turn = rand() % 2;
    int i = 0;

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
                if (crit) 
                    damage *= 1.5;

                wild->hp -= damage;
                printf("%s는 %d의 데미지를 입었다.\n", wild->name, damage);
                
                if (add == 1.5) 
                    printf("효과가 굉장했다!\n");
                else if 
                    (add == 0.5) printf("효과가 별로인 듯 하다.\n");
                if (crit) 
                    printf("급소에 맞았다!\n");

                if (wild->hp <= 0) 
                    break;
                turn = 1;
            }
            else 
            {
                wild_attack(player, wild, &add, &damage);
                if (player->poketmon.hp <= 0)
                {
                    player->poketmon.hp = 0;
                    my_poketmon_die(player, wild, wild_max_hp);
                
                    if (change_poketmon(player) == 1)
                    {
                        printf("%s(이)가 새롭게 등장했다!\n", player->poketmon.name);
                        return; 
                    }
                    else
                        return; 
                }
                turn = 0;
            }
            i++;
        }
        if (wild->hp <= 0)
        {
            printf("야ㅎ생 포켓몬 뒤짐\n");
            wild->hp = 0;
            battle_poketmon(player, wild, wild_max_hp);
        }    
}
void attack_menu(t_player_info *player, t_my *wild, int slect, int wild_max_hp)
{
    if (slect == 1)
    {
        int turn = rand() % 2;
        
        attack_menu_choice_one(player, wild, wild_max_hp);
    }
    
}

int poketmon_master(t_player_info *player)
{
    char choice;

    printf("\n========================================\n");
    if (player->poket_cnt == 6)
        printf("포켓몬 마스터가 되었다!\n");
    else
        printf("포켓몬 도감 (현재 %d마리)\n", player->poket_cnt);

    int i = 0;
    while (i < player->poket_cnt)
    {
        printf("%s  %d/%d\n", 
                player->my_list[i].name, 
                player->my_list[i].hp, 
                player->my_list[i].my_full_hp);
        i++;
    }

    printf("\n\n");
    printf("포켓몬볼 x %d\n", player->monster_ball);
    printf("상처약 x %d\n", player->hiller);
    printf("\n지갑  %d원\n", player->money);
    printf("\n========================================\n");
    
    printf("게임을 재시작하겠습니까? (Y/N)\n>> ");
    while(getchar() != '\n'); // 버퍼 비우기
    scanf(" %c", &choice);

    if (choice == 'y' || choice == 'Y')
        return 1; // 재시작 시그널
    else
        return 0; // 종료 시그널
}

int play_adventure(t_player_info *player, t_data *list, int cnt)
{
    int i = rand() % cnt;
    t_my wild;
    int flag = 0;

    if (player->poket_cnt > 0)
        player->poketmon = player->my_list[0];
    printf("========================================\n");
    printf("포켓몬을 탐색하는중 . . .\n");

    int wait= (rand() % 5) + 1;
    // sleep(wait);


    strcpy(wild.name, list[i].name);
    strcpy(wild.type, list[i].type);
    
    printf("mmmmmmmm\n");
    wild.attack = list[i].min_attack + (rand() % 101);
    wild.hp = list[i].min_hp + (rand() % 151);
    int wild_max_hp = wild.hp;
    
    printf("========================================\n");
    printf("앗! 야생의 %s이(가) 나타났다!\n", wild.name);
    printf("========================================\n");
    
    int j = 0;
    int found_alive = 0;
    while (j < player->poket_cnt)
    {
        if (player->my_list[j].hp > 0)
        {
            player->poketmon = player->my_list[j]; // 살아있는 애를 전투용으로 설정
            found_alive = 1;
            break;
        } 
        j++;
    }

    while(player->poketmon.hp >= 0 && wild.hp >= 0)
    {
        if (wild.hp < 0) 
            wild.hp = 0;

        if (flag == 0)
        {
            battle_poketmon(player, &wild, wild_max_hp);
        }
            printf("========================================\n");
            printf("무엇을 해야할까?\n1. 공격 2. 가방열기 3. 도망치기\n>> ");
            printf("========================================\n");
            int slect;
            scanf("%d", &slect);
        if (slect == 1) 
        {
            attack_menu(player, &wild, slect, wild_max_hp);
            flag = 0;
            if(player->poketmon.hp == 0)
                break;
            else if (wild.hp == 0)
            {
                int mon = (rand() % 201 + 300);
                player->money += mon;
                int sync_idx = 0;
                while (sync_idx < player->poket_cnt)
                {
                    if (strcmp(player->my_list[sync_idx].name, player->poketmon.name) == 0)
                    {
                        player->my_list[sync_idx].hp = player->poketmon.hp;
                         break;
                    }
                sync_idx++;
            }
                printf("야생포켓몬을 물리쳤습니다! \n%d 을 벌었습니다! \n", mon);
                break;
            }
        }
        else if (slect == 2)
        {
            int item_choice;
            printf("\n[ 가방 목록 ]\n");
            printf("1. 몬스터볼 x %d\n", player->monster_ball);
            printf("2. 회복약 x %d\n", player->hiller);
            printf("0. 뒤로가기\n>> ");
            scanf("%d", &item_choice);
            
            if (item_choice == 1)
            {
                if (player->monster_ball > 0)
                {
                    player->monster_ball--;
                    double wild_hp_rate = ((double)wild.hp / wild_max_hp) * 100;
                    int catch_rate = 100 - (int)wild_hp_rate;
                    printf("몬스터볼을 던졌다! (성공 확률: %d%%)\n", catch_rate);

                    if ((rand() % 100) < catch_rate)
                    {
                        char nickname;
                        printf("앗! %s을(를) 잡았다!\n", wild.name);
                        printf("별명을 지어주시겠습니까? (y/n): ");
          
                        while(getchar() != '\n');
                        scanf(" %c", &nickname);
                        if (nickname == 'y' || nickname == 'Y') 
                        {
                            printf("새로운 별명 입력: ");
                            scanf("%s", wild.name);
                        }
                        if (player->poket_cnt < 6)
                        {
                            player->my_list[player->poket_cnt] = wild;
                            player->my_list[player->poket_cnt].my_full_hp = wild_max_hp; 
                            player->poket_cnt++;
                            printf("%s(이)가 리스트에 추가되었습니다! (현재 %d마리)\n", wild.name, player->poket_cnt);
                            if (player->poket_cnt == 6)
                            {
                                if (poketmon_master(player) == 1)
                                {
                                    
                                    return (1);
                                }
                                return(0);
                            }
                            break;
                        }
                        int idx = 0;
                        while (idx < player->poket_cnt)
                        {
                            if (strcmp(player->my_list[idx].name, player->poketmon.name) == 0)
                            {
                                player->my_list[idx].hp = player->poketmon.hp;
                                break;
                            }
                            idx++;
                        }
                        return(0);
                        wild.hp = 0; // 전투 종료를 위해 wild hp 조절
                    }
                    else
                        printf("포켓몬이 몬스터볼에서 빠져나왔다!\n");
                }
                else printf("몬스터볼이 없습니다!\n");
            }
            else if (item_choice == 2)
            {
                if (player->hiller > 0)
                {
                    player->hiller--;
                    int heal_amount = (int)(player->poketmon.my_full_hp * 0.3);
                    player->poketmon.hp += heal_amount;
            
                    if (player->poketmon.hp > player->poketmon.my_full_hp)
                        player->poketmon.hp = player->poketmon.my_full_hp;
                
                    printf("%s의 체력이 %d만큼 회복되었다!\n", player->poketmon.name, heal_amount);
                 }
                else printf("회복약이 없습니다!\n");
            }
            flag = 0;
        }
        else if (slect == 3)
        {
            int escape_percent;
            double hp_percent = ((double)player->poketmon.hp / player->poketmon.my_full_hp) * 100;
            if (hp_percent >= 100.0)
                escape_percent = 10;
            else if (hp_percent >= 50.0)
                escape_percent = 40;
            else if (hp_percent >= 20.0)
                escape_percent = 60;
            else
                escape_percent = 70;

            int dice = rand() % 100;
            if (dice < escape_percent)
            {
                printf("도망치는데 성공했다!\n");
                int index = 0;
                while (index < player->poket_cnt)
                {
                    if (strcmp(player->my_list[index].name, player->poketmon.name) == 0)
                    {
                        player->my_list[index].hp = player->poketmon.hp;
                        break;
                    }
                    index++;
                    }
                break;
            }
            else
            {
                float add = 1.0;
                int damage;
                flag = 0;
                printf("도망치는데 실패했다!\n");
                printf("fuck = %d\n", player->poketmon.hp);
                wild_attack(player, &wild, &add, &damage);
                if (player->poketmon.hp <= 0)
                {
                    player->poketmon.hp = 0;
                    battle_poketmon(player, &wild, wild_max_hp);
                    change_poketmon(player);
                    break;
                }
            }
        }
    }
    return (0);
}

void hill_poketmon(t_player_info *player)
{
    printf("포켓몬 회복중 ...\n");
    sleep(2);

    int i = 0;
    while (i < player->poket_cnt)
    {
        player->my_list[i].hp = player->my_list[i].my_full_hp;
        i++;
    }
    if (player->poket_cnt > 0)
    {
        player->poketmon = player->my_list[0];
    }
    
    printf("모든 포켓몬이 회복되었습니다!\n");
}

void poketmon_shop(t_player_info *player, t_data *list)
{
    int choice;
    int cnt;
    int price;
    while(1)
    {
        printf("\n========================================\n");
        printf("상점\t\t\t지갑 : %d원\n", player->money);
        printf("1. 포켓몬볼  1000원\n");
        printf("2. 상처약    500원\n");
        printf("========================================\n");
        printf("무엇을 구매할까? (나가기 0)\n>> ");

        scanf("%d", &choice);
        if (choice == 0)
            break;
        if (choice == 1) 
        {
            printf("포켓몬볼을 몇 개 구매할까? (취소 -1)\n>> ");
            price = 1000;
        } 
        else if (choice == 2) 
        {
            printf("상처약을 몇 개 구매할까? (취소 -1)\n>> ");
            price = 500;
        } 
        else 
        {
            printf("잘못된 선택입니다.\n");
            continue;
        }
        
        scanf("%d", &cnt);
        if (cnt <= 0) continue;

        int total_price = price * cnt;

        if (player->money >= total_price) 
        {
            player->money -= total_price; 
            if (choice == 1) 
                player->monster_ball += cnt;
            else 
                player->hiller += cnt;
            
            printf("성공적으로 구매하였다! (잔액 %d원)\n", player->money);
        } 
        else
            printf("돈이 부족합니다!\n");
    }
}

void show_poketmon_book(t_player_info *player)
{
    printf("\n========================================\n");
    printf("             [ 포켓몬 도감 ]             \n");
    printf("========================================\n");
    
    printf("보유 중인 포켓몬 수: %d / 6\n", player->poket_cnt);
    printf("----------------------------------------\n");

    if (player->poket_cnt == 0)
        printf("도감이 없습니다\n");
    else
    {
        int i = 0;
        while (i < player->poket_cnt)
        {
            printf("%d. %s\n", i + 1, player->my_list[i].name);
            printf("   타입: %s\n", player->my_list[i].type);
            printf("   공격력: %d\n", player->my_list[i].attack);
            printf("   체력: %d / %d\n", player->my_list[i].hp, player->my_list[i].my_full_hp);
            
            if (player->my_list[i].hp <= 0) 
                printf("   상태: [ 기절함 ]\n");
            
            else
                printf("   상태: [ 건강함 ]\n");
            printf("----------------------------------------\n");
            i++;
        }
    }
    printf("========================================\n");
    while(getchar() != '\n');
    getchar();
}


void save_game(t_player_info *player)
{
    FILE *fp = fopen("save_data.txt", "w");
    if (fp == NULL)
    {
        printf("\n[오류] 저장 파일을 생성할 수 없습니다.\n");
        return;
    }

    fprintf(fp, "%d %d %d %d\n", 
            player->money, 
            player->monster_ball, 
            player->hiller, 
            player->poket_cnt);

    int i = 0;
    while (i < player->poket_cnt)
    {
        fprintf(fp, "%s %s %d %d %d\n",
                player->my_list[i].name,
                player->my_list[i].type,
                player->my_list[i].attack,
                player->my_list[i].hp,
                player->my_list[i].my_full_hp);
        i++;
    }

    fclose(fp); // 파일 닫기
    printf("\n========================================\n");
    printf("진행 상황이 성공적으로 저장되었습니다!\n");
    printf("========================================\n");
}


int load_game(t_player_info *player)
{
    FILE *fp = fopen("save_data.txt", "r");
    
    if (fp == NULL)
    {
        printf("\n[오류] 저장된 데이터가 없습니다!\n");
        return 0; 
    }

    if (fscanf(fp, "%d %d %d %d", 
           &player->money, 
           &player->monster_ball, 
           &player->hiller, 
           &player->poket_cnt) == EOF) 
    {
        fclose(fp);
        return 0;
    }

    int i = 0;
    while (i < player->poket_cnt)
    {
        fscanf(fp, "%s %s %d %d %d",
               player->my_list[i].name,
               player->my_list[i].type,
               &player->my_list[i].attack,
               &player->my_list[i].hp,
               &player->my_list[i].my_full_hp);
        i++;
    }

    if (player->poket_cnt > 0)
    {
        player->poketmon = player->my_list[0];
    }

    fclose(fp);
    printf("\n========================================\n");
    printf("성공적으로 데이터를 불러왔습니다!\n");
    printf("========================================\n");
    return 1; 
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
        
    while (1)
    {
        printf("========================================\n");
        printf("       K. Knock Pokemon Game\n\n");
        printf("         press enter to start\n");
        printf("========================================\n");
        getchar();
        printf("========================================\n");
        printf("    1. 새로하기    2. 이어하기\n");
        printf(">> ");
        scanf("%d", &menu);
        if (menu == 1 ) 
        {
            start_new_game(&player, list, cnt);
        }
            
        else if (menu == 2)
        {
            if (load_game(&player) == 0)
            {
                continue; 
            }
        }
        else
            continue;

        while(1)
            {
                printf("\n========================================\n");
                printf("모험을 진행하시겠습니까?\n");
                printf("1. 네  2. 저장  3. 상점  4. 포켓몬센터  5. 포켓몬도감\n");
                printf(">> ");
                if (player.poketmon.hp != 0)
                {
                    int action;
                    scanf("%d", &action);
                    if (action == 1) 
                    {
                        if (play_adventure(&player, list, cnt) == 1)
                            break;
                    }  
                    else if (action == 2) 
                        save_game(&player);
                    else if (action == 3)
                    {
                        printf("////상점\n");
                        poketmon_shop(&player, list);
                    }
                    else if (action == 4)
                    {
                        hill_poketmon(&player);
                    }
                    else if (action == 5)
                    {
                        show_poketmon_book(&player);
                    }
                }
                else if (player.poketmon.hp == 0)
                {
                    int action;
                    scanf("%d", &action);
                    if (action == 1)
                        printf("포켓몬을 회복하세요.\n");
                    else if (action == 2)
                        save_game(&player);
                    else if (action == 3)
                    {
                        printf("////상점\n");
                        poketmon_shop(&player, list);
                    }
                    else if (action == 4)
                    {
                        hill_poketmon(&player);
                    }
                    else if (action == 5)
                    {
                        show_poketmon_book(&player);
                    }
                }
            }
    }
    free(list);
    return (0);
}