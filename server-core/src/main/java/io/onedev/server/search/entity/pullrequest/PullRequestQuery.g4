grammar PullRequestQuery;

query
    : WS* criteria WS* (WS OrderBy WS+ order (WS* orderOperator=Comma WS* order)* WS*)? EOF
    | WS* OrderBy WS+ order (WS* orderOperator=Comma WS* order)* WS* EOF
    | WS* EOF
    ;

criteria
	: operator=(Open|Merged|Discarded|NeedMyAction|AssignedToMe|SubmittedByMe|WatchedByMe|IgnoredByMe|CommentedByMe|ToBeReviewedByMe|ToBeChangedByMe|ToBeMergedByMe|RequestedForChangesByMe|ApprovedByMe|MentionedMe|ReadyToMerge|SomeoneRequestedForChanges|HasPendingReviews|HasUnsuccessfulBuilds|HasUnfinishedBuilds|HasMergeConflicts) #OperatorCriteria
    | operator=(NeedActionOf|ToBeReviewedBy|ToBeChangedBy|ToBeMergedBy|AssignedTo|ApprovedBy|RequestedForChangesBy|SubmittedBy|WatchedBy|IgnoredBy|CommentedBy|Mentioned|IncludesCommit|IncludesIssue) WS+ criteriaValue=multipleQuoted #OperatorValueCriteria
    | criteriaField=Quoted WS+ operator=(Is|IsNot|IsGreaterThan|IsLessThan|IsUntil|IsSince|Contains) WS+ criteriaValue=multipleQuoted #FieldOperatorValueCriteria
    | Reference #ReferenceCriteria
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

Open
	: 'open'
	;

Merged
    : 'merged'
    ;

Discarded
    : 'discarded'
    ;

NeedMyAction
    : 'need' WS+ 'my' WS+ 'action'
    ;

ToBeReviewedByMe
    : 'to' WS+ 'be' WS+ 'reviewed' WS+ 'by' WS+ 'me'
    ;

ToBeChangedByMe
    : 'to' WS+ 'be' WS+ 'changed' WS+ 'by' WS+ 'me'
    ;

ToBeMergedByMe
    : 'to' WS+ 'be' WS+ 'merged' WS+ 'by' WS+ 'me'
    ;

RequestedForChangesByMe
    : 'requested' WS+ 'for' WS+ 'changes' WS+ 'by' WS+ 'me'
    ;

AssignedToMe
	: 'assigned' WS+ 'to' WS+ 'me'
	;
	
ApprovedByMe
    : 'approved' WS+ 'by' WS+ 'me'
    ;

SubmittedByMe
	: 'submitted' WS+ 'by' WS+ 'me'
	;

WatchedByMe
    : 'watched' WS+ 'by' WS+ 'me'
    ;

IgnoredByMe
    : 'ignored' WS+ 'by' WS+ 'me'
    ;

CommentedByMe
	: 'commented' WS+ 'by' WS+ 'me'
	;

MentionedMe
    : 'mentioned' WS+ 'me'
    ;

ReadyToMerge
    : 'ready' WS+ 'to' WS+ 'merge'
    ;

SomeoneRequestedForChanges
    : 'someone' WS+ 'requested' WS+ 'for' WS+ 'changes'
    ;

HasPendingReviews
    : 'has' WS+ 'pending' WS+ 'reviews'
    ;

HasUnsuccessfulBuilds
    : 'has' WS+ 'unsuccessful' WS+ 'builds'
    ;

HasUnfinishedBuilds
    : 'has' WS+ 'unfinished' WS+ 'builds'
    ;

HasMergeConflicts
    : 'has' WS+ 'merge' WS+ 'conflicts'
    ;

NeedActionOf
    : 'need' WS+ 'action' WS+ 'of'
    ;

ToBeReviewedBy
    : 'to' WS+ 'be' WS+ 'reviewed' WS+ 'by'
    ;

ToBeChangedBy
    : 'to' WS+ 'be' WS+ 'changed' WS+ 'by'
    ;

ToBeMergedBy
    : 'to' WS+ 'be' WS+ 'merged' WS+ 'by'
    ;

RequestedForChangesBy
    : 'requested' WS+ 'for' WS+ 'changes' WS+ 'by'
    ;

AssignedTo
	: 'assigned' WS+ 'to'
	;
	
ApprovedBy
    : 'approved' WS+ 'by'
    ;

SubmittedBy
	: 'submitted' WS+ 'by'
    ;

WatchedBy
    : 'watched' WS+ 'by'
    ;

IgnoredBy
    : 'ignored' WS+ 'by'
    ;

CommentedBy
	: 'commented' WS+ 'by'
    ;

Mentioned
    : 'mentioned'
    ;
	    
IncludesCommit
	: 'includes' WS+ 'commit'
    ;    
    
IncludesIssue
	: 'includes' WS+ 'issue'
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

Contains
	: 'contains'
    ;

IsGreaterThan
	: 'is' WS+ 'greater' WS+ 'than'
    ;

IsLessThan
	: 'is' WS+ 'less' WS+ 'than'
    ;

IsSince
	: 'is' WS+ 'since'
    ;

IsUntil
	: 'is' WS+ 'until'
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

Reference
    : ([a-zA-Z0-9_]([a-zA-Z0-9_\-/.]*[a-zA-Z0-9_])?)? '#' [0-9]+ | [A-Z][A-Z]+ '-' [0-9]+
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
