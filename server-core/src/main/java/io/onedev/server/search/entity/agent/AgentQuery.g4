grammar AgentQuery;

query
    : WS* criteria WS* (WS OrderBy WS+ order (WS* orderOperator=Comma WS* order)* WS*)? EOF
    | WS* OrderBy WS+ order (WS* orderOperator=Comma WS* order)* WS* EOF
    | WS* EOF
    ;

criteria
	: operator=(Online|Offline|Paused|HasRunningBuilds) #OperatorCriteria
	| operator=(HasAttribute|NotUsedSince|EverUsedSince|RanBuild) WS+ criteriaValue=multipleQuoted #OperatorValueCriteria
    | criteriaField=Quoted WS+ operator=(Is|IsNot) WS+ criteriaValue=multipleQuoted #FieldOperatorValueCriteria
    | criteria WS+ And WS+ criteria	#AndCriteria
    | criteria WS+ Or WS+ criteria #OrCriteria
    | Not WS* LParens WS* criteria WS* RParens #NotCriteria
    | LParens WS* criteria WS* RParens #ParensCriteria
    | Fuzzy #FuzzyCriteria
    ;

order
	: orderField=Quoted WS* (WS+ direction=(Asc|Desc))?
	;

multipleQuoted
    : Quoted(WS* Comma WS* Quoted)*
    ;

Online
	: 'online'
	;

Offline
	: 'offline'
	;

Paused
	: 'paused'
	;

HasRunningBuilds
    : 'has' WS+ 'running' WS+ 'builds'
    ;

HasAttribute
    : 'has' WS+ 'attribute'
    ;
    
NotUsedSince
    : 'not' WS+ 'used' WS+ 'since'
    ;
    
EverUsedSince
    : 'ever' WS+ 'used' WS+ 'since'
    ;

SelectedByExecutor
	: 'selected' WS+ 'by' WS+ 'executor'
	;
	
RanBuild
	: 'ran' WS+ 'build'
	;

OrderBy
    : 'order' WS+ 'by'
    ;

Is
	: 'is'
	;

IsNot
    : 'is' WS+ 'not'
    ;

And
	: 'and'
	;

Or
	: 'or'
	;

Not
	: 'not'
	;

Asc
	: 'asc'
	;

Desc
	: 'desc'
	;

LParens
	: '('
	;

RParens
	: ')'
	;

Quoted
    : '"' ('\\'.|~[\\"])+? '"'
    ;

Comma
	: ','
	;

WS
    : ' '
    ;

Fuzzy
    : '~' ('\\'.|~[~])+? '~'
    ;

Identifier
	: [a-zA-Z0-9:_/\\+\-;]+
	;
